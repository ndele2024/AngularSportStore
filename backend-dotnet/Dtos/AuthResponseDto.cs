namespace SportStore.Api.Dtos;

public record AuthResponseDto(
    bool Success,
    string? Token,
    UserDto? User,
    string? Message
);
