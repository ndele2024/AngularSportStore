using SportStore.Api.Dtos;
using SportStore.Api.Models;

namespace SportStore.Api.Services;

public class StoreMapper
{
    public ProductDto ToProductDto(Product product) =>
        new(product.Id, product.Name, product.Category, product.Description, product.Price, product.ImageUrl);

    public UserDto ToUserDto(AppUser user) =>
        new(
            user.Id,
            user.Role.ToString().ToLowerInvariant(),
            user.Nom,
            user.Prenom,
            user.Adresse,
            user.Telephone,
            user.Username,
            ToStoredCartFromCartLines(user.CartLines)
        );

    public OrderDto ToOrderDto(CustomerOrder order) =>
        new()
        {
            Id = order.Id,
            UserId = order.UserId,
            Username = order.Username,
            Nom = order.Nom,
            Prenom = order.Prenom,
            Adresse = order.Adresse,
            Telephone = order.Telephone,
            CreatedAt = DateTime.SpecifyKind(order.CreatedAt, DateTimeKind.Utc),
            Total = order.Total,
            ItemCount = order.ItemCount,
            Shipped = order.Shipped,
            Cart = ToStoredCartFromOrderLines(order.Lines)
        };

    public StoredCartDto ToStoredCartFromCartLines(IEnumerable<CartLine> lines)
    {
        var list = lines
            .Select(line => new StoredCartLineDto(ToProductDto(line.Product), line.Quantity))
            .ToList();

        var itemCount = list.Sum(line => line.Quantity ?? 0);
        var total = list.Sum(line => (line.Product?.Price ?? 0m) * (line.Quantity ?? 0));
        return new StoredCartDto(list, itemCount, total);
    }

    public StoredCartDto ToStoredCartFromOrderLines(IEnumerable<OrderLine> lines)
    {
        var list = lines
            .Select(line => new StoredCartLineDto(ToProductDto(line.Product), line.Quantity))
            .ToList();

        var itemCount = list.Sum(line => line.Quantity ?? 0);
        var total = list.Sum(line => (line.Product?.Price ?? 0m) * (line.Quantity ?? 0));
        return new StoredCartDto(list, itemCount, total);
    }
}
