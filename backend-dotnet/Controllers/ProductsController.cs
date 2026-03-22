using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SportStore.Api.Data;
using SportStore.Api.Dtos;
using SportStore.Api.Models;
using SportStore.Api.Services;

namespace SportStore.Api.Controllers;

[ApiController]
public class ProductsController(SportStoreDbContext dbContext, StoreMapper mapper) : ControllerBase
{
    [HttpGet("products")]
    [AllowAnonymous]
    public async Task<ActionResult<IEnumerable<ProductDto>>> GetProducts()
    {
        var products = await dbContext.Products
            .OrderBy(p => p.Id)
            .ToListAsync();

        return Ok(products.Select(mapper.ToProductDto));
    }

    [HttpPost("products")]
    [Authorize(Roles = "Admin")]
    public async Task<ActionResult<ProductDto>> CreateProduct([FromBody] ProductDto dto)
    {
        var product = Apply(new Product(), dto);
        dbContext.Products.Add(product);
        await dbContext.SaveChangesAsync();
        return StatusCode(StatusCodes.Status201Created, mapper.ToProductDto(product));
    }

    [HttpPut("products/{id:long}")]
    [Authorize(Roles = "Admin")]
    public async Task<ActionResult<ProductDto>> UpdateProduct(long id, [FromBody] ProductDto dto)
    {
        var product = await dbContext.Products.FindAsync(id);
        if (product is null)
        {
            return NotFound(new ErrorResponseDto(false, "Product not found"));
        }

        Apply(product, dto);
        await dbContext.SaveChangesAsync();
        return Ok(mapper.ToProductDto(product));
    }

    [HttpDelete("products/{id:long}")]
    [Authorize(Roles = "Admin")]
    public async Task<IActionResult> DeleteProduct(long id)
    {
        var product = await dbContext.Products.FindAsync(id);
        if (product is null)
        {
            return NotFound(new ErrorResponseDto(false, "Product not found"));
        }

        dbContext.Products.Remove(product);
        await dbContext.SaveChangesAsync();
        return NoContent();
    }

    private static Product Apply(Product product, ProductDto dto)
    {
        product.Name = dto.Name ?? string.Empty;
        product.Category = dto.Category ?? string.Empty;
        product.Description = dto.Description ?? string.Empty;
        product.Price = dto.Price ?? 0m;
        product.ImageUrl = dto.ImageUrl ?? string.Empty;
        return product;
    }
}
