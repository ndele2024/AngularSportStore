namespace SportStore.Api.Dtos;

public record UserDto(
    long Id,
    string Role,
    string Nom,
    string Prenom,
    string Adresse,
    string Telephone,
    string Username,
    StoredCartDto Cart
);
