namespace SportStore.Api.Dtos;

public class OrderDto
{
    public long? Id { get; set; }
    public long? UserId { get; set; }
    public string? Username { get; set; }
    public string? Nom { get; set; }
    public string? Prenom { get; set; }
    public string? Adresse { get; set; }
    public string? Telephone { get; set; }
    public DateTime? CreatedAt { get; set; }
    public decimal? Total { get; set; }
    public int? ItemCount { get; set; }
    public string? Status { get; set; }
    public string? PaymentStatus { get; set; }
    public string? PaymentMethod { get; set; }
    public string? PaymentReference { get; set; }
    public string? PaymentLast4 { get; set; }
    public string? InvoiceNumber { get; set; }
    public DateTime? DeliveredAt { get; set; }
    public bool Shipped { get; set; }
    public StoredCartDto? Cart { get; set; }
}
