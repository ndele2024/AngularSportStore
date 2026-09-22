using System.Net.Http.Headers;
using System.Net.Http.Json;
using SportStore.Api.Dtos;
using SportStore.Api.Tests.Infrastructure;

namespace SportStore.Api.Tests.Integration;

public class OrderLifecycleIntegrationTests : IClassFixture<TestWebApplicationFactory>
{
    private readonly HttpClient _client;

    public OrderLifecycleIntegrationTests(TestWebApplicationFactory factory)
    {
        _client = factory.CreateClient();
    }

    [Fact]
    public async Task AuthenticatedUserCanCreateOrderAndDownloadInvoice()
    {
        var token = await LoginAndGetTokenAsync("jane", "password");
        _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", token);

        var response = await _client.PostAsJsonAsync("/orders", new OrderDto
        {
            Nom = "Doe",
            Prenom = "Jane",
            Adresse = "25 Market Street",
            Telephone = "555-111-2233",
            PaymentStatus = "PAYE",
            PaymentMethod = "CARTE_CREDIT",
            PaymentReference = "PAY-TEST-300",
            PaymentLast4 = "4242",
            Cart = new StoredCartDto(
                [
                    new StoredCartLineDto(new ProductDto(1, null, null, null, null, null), 2)
                ],
                2,
                550m)
        });

        response.EnsureSuccessStatusCode();
        var order = await response.Content.ReadFromJsonAsync<OrderDto>();
        Assert.NotNull(order);
        Assert.Equal("EN_TRAITEMENT", order!.Status);
        Assert.Equal("PAYE", order.PaymentStatus);
        Assert.False(string.IsNullOrWhiteSpace(order.InvoiceNumber));

        var pdf = await _client.GetAsync($"/orders/{order.Id}/invoice");
        pdf.EnsureSuccessStatusCode();
        Assert.Equal("application/pdf", pdf.Content.Headers.ContentType?.MediaType);
    }

    [Fact]
    public async Task AdminCanMarkOrderAsDelivered()
    {
        var userToken = await LoginAndGetTokenAsync("jane", "password");
        _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", userToken);

        var creation = await _client.PostAsJsonAsync("/orders", new OrderDto
        {
            Nom = "Doe",
            Prenom = "Jane",
            Adresse = "25 Market Street",
            Telephone = "555-111-2233",
            PaymentStatus = "PAYE",
            PaymentMethod = "CARTE_CREDIT",
            PaymentReference = "PAY-TEST-301",
            PaymentLast4 = "1111",
            Cart = new StoredCartDto(
                [
                    new StoredCartLineDto(new ProductDto(2, null, null, null, null, null), 1)
                ],
                1,
                48.95m)
        });

        creation.EnsureSuccessStatusCode();
        var order = await creation.Content.ReadFromJsonAsync<OrderDto>();
        Assert.NotNull(order);

        var adminToken = await LoginAndGetTokenAsync("admin", "secret");
        _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", adminToken);

        var delivery = await _client.PutAsJsonAsync($"/orders/{order!.Id}", new OrderDto
        {
            Status = "LIVRE",
            Shipped = true
        });

        delivery.EnsureSuccessStatusCode();
        var updated = await delivery.Content.ReadFromJsonAsync<OrderDto>();
        Assert.NotNull(updated);
        Assert.Equal("LIVRE", updated!.Status);
        Assert.True(updated.Shipped);
        Assert.NotNull(updated.DeliveredAt);
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
