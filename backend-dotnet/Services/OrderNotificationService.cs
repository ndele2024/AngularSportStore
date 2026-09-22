using System.Globalization;
using System.Text;
using SportStore.Api.Models;

namespace SportStore.Api.Services;

public class OrderNotificationService(ILogger<OrderNotificationService> logger)
{
    public async Task SendOrderConfirmationAsync(CustomerOrder order)
    {
        await WriteEmailAsync("order-confirmation", order, BuildOrderConfirmation(order));
    }

    public async Task SendDeliveryConfirmationAsync(CustomerOrder order)
    {
        await WriteEmailAsync("delivery-confirmation", order, BuildDeliveryConfirmation(order));
    }

    private async Task WriteEmailAsync(string prefix, CustomerOrder order, string body)
    {
        var outputDir = Path.Combine(Directory.GetCurrentDirectory(), "build", "emails");
        Directory.CreateDirectory(outputDir);
        var path = Path.Combine(outputDir, $"{prefix}-{order.Id}.html");
        await File.WriteAllTextAsync(path, body, Encoding.UTF8);
        logger.LogInformation("Email prepared for {Username} at {Path}", order.Username, path);
    }

    private static string BuildOrderConfirmation(CustomerOrder order) => $"""
        <html>
          <body style="font-family:Arial,sans-serif;background:#f4f8fc;padding:24px;color:#173250">
            <div style="max-width:760px;margin:auto;background:#fff;border-radius:20px;padding:24px">
              <h1 style="margin-top:0">Merci pour votre commande</h1>
              <p>Bonjour {Safe(order.Prenom)} {Safe(order.Nom)}, votre commande <strong>#{order.Id}</strong> a bien ete payee.</p>
              <p>Facture: <strong>{Safe(order.InvoiceNumber)}</strong><br/>Paiement: <strong>{Safe(order.PaymentReference)}</strong><br/>Montant: <strong>${order.Total.ToString("0.00", CultureInfo.InvariantCulture)}</strong></p>
              <h2>Articles</h2>
              {BuildProductsHtml(order)}
            </div>
          </body>
        </html>
        """;

    private static string BuildDeliveryConfirmation(CustomerOrder order) => $"""
        <html>
          <body style="font-family:Arial,sans-serif;background:#f4f8fc;padding:24px;color:#173250">
            <div style="max-width:760px;margin:auto;background:#fff;border-radius:20px;padding:24px">
              <h1 style="margin-top:0">Votre commande a ete livree</h1>
              <p>Bonjour {Safe(order.Prenom)} {Safe(order.Nom)}, votre commande <strong>#{order.Id}</strong> est maintenant marquee comme livree.</p>
              <p>Date de livraison: <strong>{(order.DeliveredAt?.ToLocalTime().ToString("yyyy-MM-dd HH:mm", CultureInfo.InvariantCulture) ?? "-")}</strong></p>
              <h2>Recapitulatif</h2>
              {BuildProductsHtml(order)}
            </div>
          </body>
        </html>
        """;

    private static string BuildProductsHtml(CustomerOrder order)
    {
        var builder = new StringBuilder();
        foreach (var line in order.Lines)
        {
            builder.Append($"""
                <div style="display:flex;gap:16px;align-items:center;padding:12px 0;border-top:1px solid #e5edf6">
                  <img src="{Safe(line.Product.ImageUrl)}" alt="{Safe(line.Product.Name)}" style="width:80px;height:80px;object-fit:cover;border-radius:14px"/>
                  <div>
                    <div style="font-weight:700">{Safe(line.Product.Name)}</div>
                    <div>Quantite: {line.Quantity}</div>
                    <div>Prix unitaire: ${line.Product.Price.ToString("0.00", CultureInfo.InvariantCulture)}</div>
                  </div>
                </div>
                """);
        }

        return builder.ToString();
    }

    private static string Safe(object? value) => value?.ToString() ?? "-";
}
