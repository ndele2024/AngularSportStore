using System.Net;
using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text.Json.Nodes;
using SportStore.Api.Dtos;
using SportStore.Api.Tests.Infrastructure;

namespace SportStore.Api.Tests.Integration;

public class UserAndProductIntegrationTests : IClassFixture<TestWebApplicationFactory>
{
    private readonly HttpClient _client;

    public UserAndProductIntegrationTests(TestWebApplicationFactory factory)
    {
        _client = factory.CreateClient();
    }

    [Fact]
    public async Task Products_AreAccessibleWithoutAuthentication()
    {
        var products = await _client.GetFromJsonAsync<List<ProductDto>>("/products");

        Assert.NotNull(products);
        Assert.NotEmpty(products!);
    }

    [Fact]
    public async Task UserCanUpdateOwnProfileAndCart()
    {
        var token = await LoginAndGetTokenAsync("jane", "password");
        _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", token);

        var request = new UpdateUserRequestDto
        {
            Nom = "Doe Updated",
            Telephone = "555-999-0000",
            Cart = new StoredCartDto(
                [
                    new StoredCartLineDto(new ProductDto(1, null, null, null, null, null), 2)
                ],
                2,
                550m
            )
        };

        var message = new HttpRequestMessage(HttpMethod.Patch, "/users/2")
        {
            Content = JsonContent.Create(request)
        };

        var response = await _client.SendAsync(message);
        response.EnsureSuccessStatusCode();

        var user = await response.Content.ReadFromJsonAsync<UserDto>();
        Assert.NotNull(user);
        Assert.Equal("Doe Updated", user!.Nom);
        Assert.Equal("555-999-0000", user.Telephone);
        Assert.Single(user.Cart.Lines!);
        Assert.Equal(2, user.Cart.Lines![0].Quantity);
    }

    [Fact]
    public async Task CreatingProductWithoutToken_IsRejected()
    {
        var response = await _client.PostAsJsonAsync("/products", new ProductDto(null, "Test", "Test", "Desc", 10m, "https://example.com/x.jpg"));

        Assert.True(response.StatusCode is HttpStatusCode.Unauthorized or HttpStatusCode.Forbidden);
    }

    private async Task<string> LoginAndGetTokenAsync(string username, string password)
    {
        var response = await _client.PostAsJsonAsync("/login", new LoginRequestDto
        {
            Username = username,
            Password = password
        });

        response.EnsureSuccessStatusCode();
        var payload = await response.Content.ReadFromJsonAsync<AuthResponseDto>();
        Assert.NotNull(payload);
        Assert.False(string.IsNullOrWhiteSpace(payload!.Token));
        return payload.Token!;
    }
}
