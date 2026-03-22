namespace SportStore.Api.Models;

public class AppUser
{
    public long Id { get; set; }
    public Role Role { get; set; } = Role.User;
    public string Nom { get; set; } = string.Empty;
    public string Prenom { get; set; } = string.Empty;
    public string Adresse { get; set; } = string.Empty;
    public string Telephone { get; set; } = string.Empty;
    public string Username { get; set; } = string.Empty;
    public string PasswordHash { get; set; } = string.Empty;
    public ICollection<CartLine> CartLines { get; set; } = new List<CartLine>();
}
