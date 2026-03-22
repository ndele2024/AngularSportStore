namespace SportStore.Api.Dtos;

public class RegisterRequestDto
{
    public string Nom { get; set; } = string.Empty;
    public string Prenom { get; set; } = string.Empty;
    public string Adresse { get; set; } = string.Empty;
    public string Telephone { get; set; } = string.Empty;
    public string Username { get; set; } = string.Empty;
    public string Password { get; set; } = string.Empty;
}
