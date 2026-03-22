namespace SportStore.Api.Options;

public class JwtOptions
{
    public string Secret { get; set; } = "dotnet-sport-store-secret-key-change-me-123456789";
    public string Issuer { get; set; } = "SportStore.Api";
    public string Audience { get; set; } = "SportStore.Frontend";
    public int ExpirationHours { get; set; } = 8;
}
