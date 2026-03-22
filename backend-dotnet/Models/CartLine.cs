namespace SportStore.Api.Models;

public class CartLine
{
    public long Id { get; set; }
    public long UserId { get; set; }
    public AppUser User { get; set; } = null!;
    public long ProductId { get; set; }
    public Product Product { get; set; } = null!;
    public int Quantity { get; set; }
}
