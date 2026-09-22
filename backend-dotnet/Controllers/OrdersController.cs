using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SportStore.Api.Data;
using SportStore.Api.Dtos;
using SportStore.Api.Extensions;
using SportStore.Api.Models;
using SportStore.Api.Services;

namespace SportStore.Api.Controllers;

[ApiController]
[Authorize]
public class OrdersController(
    SportStoreDbContext dbContext,
    StoreMapper mapper,
    InvoicePdfService invoicePdfService,
    OrderNotificationService orderNotificationService) : ControllerBase
{
    [HttpGet("orders")]
    public async Task<ActionResult<IEnumerable<OrderDto>>> GetOrders()
    {
        IQueryable<CustomerOrder> query = dbContext.Orders
            .Include(o => o.Lines)
            .ThenInclude(line => line.Product)
            .OrderByDescending(o => o.CreatedAt);

        if (!User.IsAdmin())
        {
            var currentUserId = User.GetUserId();
            query = query.Where(o => o.UserId == currentUserId);
        }

        var orders = await query.ToListAsync();
        return Ok(orders.Select(mapper.ToOrderDto));
    }

    [HttpGet("orders/{id:long}/invoice")]
    public async Task<IActionResult> DownloadInvoice(long id)
    {
        var order = await FindAuthorizedOrderAsync(id);
        if (order is null)
        {
            return NotFound(new ErrorResponseDto(false, "Order not found"));
        }

        var pdf = invoicePdfService.Generate(mapper.ToOrderDto(order));
        return File(pdf, "application/pdf", $"facture-{id}.pdf");
    }

    [HttpPost("orders")]
    public async Task<ActionResult<OrderDto>> CreateOrder([FromBody] OrderDto request)
    {
        var currentUserId = User.GetUserId();
        var user = await dbContext.Users.FirstOrDefaultAsync(u => u.Id == currentUserId);
        if (user is null)
        {
            return NotFound(new ErrorResponseDto(false, "User not found"));
        }

        var lines = await BuildOrderLinesAsync(request.Cart);
        var order = new CustomerOrder
        {
            UserId = user.Id,
            Username = user.Username,
            Nom = request.Nom ?? user.Nom,
            Prenom = request.Prenom ?? user.Prenom,
            Adresse = request.Adresse ?? user.Adresse,
            Telephone = request.Telephone ?? user.Telephone,
            CreatedAt = request.CreatedAt?.ToUniversalTime() ?? DateTime.UtcNow,
            ItemCount = lines.Sum(l => l.Quantity),
            Total = lines.Sum(l => l.Product.Price * l.Quantity),
            Status = "EN_TRAITEMENT",
            PaymentStatus = string.IsNullOrWhiteSpace(request.PaymentStatus) ? "PAYE" : request.PaymentStatus,
            PaymentMethod = string.IsNullOrWhiteSpace(request.PaymentMethod) ? "CARTE_CREDIT" : request.PaymentMethod,
            PaymentReference = string.IsNullOrWhiteSpace(request.PaymentReference) ? GeneratePaymentReference() : request.PaymentReference,
            PaymentLast4 = string.IsNullOrWhiteSpace(request.PaymentLast4) ? "0000" : request.PaymentLast4,
            InvoiceNumber = GenerateInvoiceNumber(user.Id),
            DeliveredAt = null,
            Shipped = false,
            Lines = lines
        };

        dbContext.Orders.Add(order);
        await dbContext.SaveChangesAsync();

        var created = await dbContext.Orders
            .Include(o => o.Lines)
            .ThenInclude(line => line.Product)
            .FirstAsync(o => o.Id == order.Id);

        await orderNotificationService.SendOrderConfirmationAsync(created);
        return StatusCode(StatusCodes.Status201Created, mapper.ToOrderDto(created));
    }

    [HttpPut("orders/{id:long}")]
    public async Task<ActionResult<OrderDto>> UpdateOrder(long id, [FromBody] OrderDto request)
    {
        var order = await dbContext.Orders
            .Include(o => o.Lines)
            .ThenInclude(line => line.Product)
            .FirstOrDefaultAsync(o => o.Id == id);

        if (order is null)
        {
            return NotFound(new ErrorResponseDto(false, "Order not found"));
        }

        if (!User.IsAdmin() && order.UserId != User.GetUserId())
        {
            return StatusCode(StatusCodes.Status403Forbidden, new ErrorResponseDto(false, "Access denied"));
        }

        if (!User.IsAdmin())
        {
            return StatusCode(StatusCodes.Status403Forbidden, new ErrorResponseDto(false, "Admin access required"));
        }

        if (request.Nom is not null) order.Nom = request.Nom;
        if (request.Prenom is not null) order.Prenom = request.Prenom;
        if (request.Adresse is not null) order.Adresse = request.Adresse;
        if (request.Telephone is not null) order.Telephone = request.Telephone;
        if (request.PaymentStatus is not null) order.PaymentStatus = request.PaymentStatus;
        if (request.PaymentMethod is not null) order.PaymentMethod = request.PaymentMethod;
        if (request.PaymentReference is not null) order.PaymentReference = request.PaymentReference;
        if (request.PaymentLast4 is not null) order.PaymentLast4 = request.PaymentLast4;

        var wasDelivered = order.Status == "LIVRE";
        var nextStatus = request.Status ?? (request.Shipped ? "LIVRE" : "EN_TRAITEMENT");
        order.Status = nextStatus;
        order.Shipped = nextStatus == "LIVRE";
        order.DeliveredAt = order.Shipped ? order.DeliveredAt ?? DateTime.UtcNow : null;

        if (request.Cart is not null)
        {
            dbContext.OrderLines.RemoveRange(order.Lines);
            order.Lines.Clear();

            var lines = await BuildOrderLinesAsync(request.Cart);
            foreach (var line in lines)
            {
                order.Lines.Add(line);
            }

            order.ItemCount = order.Lines.Sum(l => l.Quantity);
            order.Total = order.Lines.Sum(l => l.Product.Price * l.Quantity);
        }

        await dbContext.SaveChangesAsync();

        var updated = await dbContext.Orders
            .Include(o => o.Lines)
            .ThenInclude(line => line.Product)
            .FirstAsync(o => o.Id == id);

        if (!wasDelivered && updated.Status == "LIVRE")
        {
            await orderNotificationService.SendDeliveryConfirmationAsync(updated);
        }

        return Ok(mapper.ToOrderDto(updated));
    }

    [HttpDelete("orders/{id:long}")]
    [Authorize(Roles = "Admin")]
    public async Task<IActionResult> DeleteOrder(long id)
    {
        var order = await dbContext.Orders.FindAsync(id);
        if (order is null)
        {
            return NotFound(new ErrorResponseDto(false, "Order not found"));
        }

        dbContext.Orders.Remove(order);
        await dbContext.SaveChangesAsync();
        return NoContent();
    }

    private async Task<CustomerOrder?> FindAuthorizedOrderAsync(long id)
    {
        var order = await dbContext.Orders
            .Include(o => o.Lines)
            .ThenInclude(line => line.Product)
            .FirstOrDefaultAsync(o => o.Id == id);

        if (order is null)
        {
            return null;
        }

        if (!User.IsAdmin() && order.UserId != User.GetUserId())
        {
            return null;
        }

        return order;
    }

    private async Task<List<OrderLine>> BuildOrderLinesAsync(StoredCartDto? cart)
    {
        var lines = new List<OrderLine>();

        foreach (var lineDto in cart?.Lines ?? [])
        {
            if (lineDto.Product?.Id is null) continue;

            var product = await dbContext.Products.FindAsync(lineDto.Product.Id.Value);
            if (product is null) continue;

            lines.Add(new OrderLine
            {
                Product = product,
                Quantity = Math.Max(lineDto.Quantity ?? 0, 0)
            });
        }

        return lines;
    }

    private static string GeneratePaymentReference() => $"PAY-{DateTimeOffset.UtcNow.ToUnixTimeMilliseconds()}";

    private static string GenerateInvoiceNumber(long userId) => $"INV-{userId}-{DateTimeOffset.UtcNow.ToUnixTimeMilliseconds()}";
}
