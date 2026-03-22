using SportStore.Api.Models;
using SportStore.Api.Services;

namespace SportStore.Api.Tests.Unit;

public class StoreMapperTests
{
    private readonly StoreMapper _mapper = new();

    [Fact]
    public void ToStoredCartFromCartLines_ComputesTotalsAndMapsProducts()
    {
        var product = new Product
        {
            Id = 1,
            Name = "Kayak",
            Category = "Watersports",
            Description = "A boat for one person",
            Price = 275m,
            ImageUrl = "https://example.com/kayak.jpg"
        };

        var cart = _mapper.ToStoredCartFromCartLines(new[]
        {
            new CartLine { Product = product, Quantity = 2 }
        });

        Assert.Equal(2, cart.ItemCount);
        Assert.Equal(550m, cart.CartPrice);
        Assert.Single(cart.Lines!);
        Assert.Equal("Kayak", cart.Lines![0].Product!.Name);
    }
}
