using System.Net.Http.Json;
using System.Text.Json.Nodes;
using SportStore.Api.Dtos;
using SportStore.Api.Tests.Infrastructure;

namespace SportStore.Api.Tests.Integration;

public class AuthIntegrationTests : IClassFixture<TestWebApplicationFactory>
{
    private readonly HttpClient _client;

    public AuthIntegrationTests(TestWebApplicationFactory factory)
    {
        _client = factory.CreateClient();
    }

    [Fact]
    public async Task Login_ReturnsToken_ForSeededAdmin()
    {
        var response = await _client.PostAsJsonAsync("/login", new LoginRequestDto
        {
            Username = "admin",
            Password = "secret"
        });

        response.EnsureSuccessStatusCode();
        var payload = await response.Content.ReadFromJsonAsync<AuthResponseDto>();

        Assert.NotNull(payload);
        Assert.True(payload!.Success);
        Assert.False(string.IsNullOrWhiteSpace(payload.Token));
        Assert.Equal("admin", payload.User!.Username);
    }

    [Fact]
    public async Task Register_ReturnsBadRequest_WhenUsernameAlreadyExists()
    {
        var response = await _client.PostAsJsonAsync("/register", new RegisterRequestDto
        {
            Nom = "Doe",
            Prenom = "Jane",
            Adresse = "25 Market Street",
            Telephone = "555-111-2233",
            Username = "jane",
            Password = "password"
        });

        Assert.Equal(System.Net.HttpStatusCode.BadRequest, response.StatusCode);
    }
}
