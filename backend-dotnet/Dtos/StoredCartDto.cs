namespace SportStore.Api.Dtos;

public record StoredCartDto(
    List<StoredCartLineDto>? Lines,
    int? ItemCount,
    decimal? CartPrice
);
