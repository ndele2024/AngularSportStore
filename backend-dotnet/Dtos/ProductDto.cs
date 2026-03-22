namespace SportStore.Api.Dtos;

public record ProductDto(
    long? Id,
    string? Name,
    string? Category,
    string? Description,
    decimal? Price,
    string? ImageUrl
);
