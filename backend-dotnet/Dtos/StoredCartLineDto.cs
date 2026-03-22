namespace SportStore.Api.Dtos;

public record StoredCartLineDto(
    ProductDto? Product,
    int? Quantity
);
