namespace SportStore.Api.Dtos;

public record ErrorResponseDto(
    bool Success,
    string Message
);
