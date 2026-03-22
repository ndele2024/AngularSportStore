namespace SportStore.Api.Models;

public class OrderLine
{
    public long Id { get; set; }
    public long OrderId { get; set; }
    public CustomerOrder Order { get; set; } = null!;
    public long ProductId { get; set; }
    public Product Product { get; set; } = null!;
    public int Quantity { get; set; }
}
