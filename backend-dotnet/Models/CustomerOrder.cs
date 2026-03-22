namespace SportStore.Api.Models;

public class CustomerOrder
{
    public long Id { get; set; }
    public long UserId { get; set; }
    public string Username { get; set; } = string.Empty;
    public string Nom { get; set; } = string.Empty;
    public string Prenom { get; set; } = string.Empty;
    public string Adresse { get; set; } = string.Empty;
    public string Telephone { get; set; } = string.Empty;
    public DateTime CreatedAt { get; set; }
    public decimal Total { get; set; }
    public int ItemCount { get; set; }
    public bool Shipped { get; set; }
    public ICollection<OrderLine> Lines { get; set; } = new List<OrderLine>();
}
