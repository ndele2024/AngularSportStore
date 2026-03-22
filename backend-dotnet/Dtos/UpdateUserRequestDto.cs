namespace SportStore.Api.Dtos;

public class UpdateUserRequestDto
{
    public string? Role { get; set; }
    public string? Nom { get; set; }
    public string? Prenom { get; set; }
    public string? Adresse { get; set; }
    public string? Telephone { get; set; }
    public string? Username { get; set; }
    public string? Password { get; set; }
    public StoredCartDto? Cart { get; set; }
}
