package com.sportstore.backend.service;

import com.sportstore.backend.domain.CustomerOrder;
import com.sportstore.backend.domain.OrderLine;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderNotificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderNotificationService.class);
  private static final DateTimeFormatter DATE_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.of("America/Toronto"));

  public void sendOrderConfirmation(CustomerOrder order) {
    writeEmail("order-confirmation", order, "Confirmation de commande", buildOrderConfirmation(order));
  }

  public void sendDeliveryConfirmation(CustomerOrder order) {
    writeEmail("delivery-confirmation", order, "Confirmation de livraison", buildDeliveryConfirmation(order));
  }

  private void writeEmail(String prefix, CustomerOrder order, String subject, String body) {
    try {
      Path outputDir = Path.of("build", "emails");
      Files.createDirectories(outputDir);
      Path file = outputDir.resolve(prefix + "-" + order.getId() + ".html");
      Files.writeString(file, body, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      LOGGER.info("Email '{}' prepared for {} at {}", subject, order.getUsername(), file.toAbsolutePath());
    } catch (IOException ex) {
      throw new IllegalStateException("Unable to prepare notification email", ex);
    }
  }

  private String buildOrderConfirmation(CustomerOrder order) {
    return """
      <html>
        <body style="font-family:Arial,sans-serif;background:#f4f8fc;padding:24px;color:#173250">
          <div style="max-width:760px;margin:auto;background:#fff;border-radius:20px;padding:24px">
            <h1 style="margin-top:0">Merci pour votre commande</h1>
            <p>Bonjour %s %s, votre commande <strong>#%s</strong> a bien ete payee.</p>
            <p>Facture: <strong>%s</strong><br/>Paiement: <strong>%s</strong><br/>Montant: <strong>$%.2f</strong></p>
            <h2>Articles</h2>
            %s
          </div>
        </body>
      </html>
      """.formatted(
      safe(order.getPrenom()),
      safe(order.getNom()),
      safe(order.getId()),
      safe(order.getInvoiceNumber()),
      safe(order.getPaymentReference()),
      order.getTotal(),
      buildProductsHtml(order)
    );
  }

  private String buildDeliveryConfirmation(CustomerOrder order) {
    return """
      <html>
        <body style="font-family:Arial,sans-serif;background:#f4f8fc;padding:24px;color:#173250">
          <div style="max-width:760px;margin:auto;background:#fff;border-radius:20px;padding:24px">
            <h1 style="margin-top:0">Votre commande a ete livree</h1>
            <p>Bonjour %s %s, votre commande <strong>#%s</strong> est maintenant marquee comme livree.</p>
            <p>Date de livraison: <strong>%s</strong></p>
            <h2>Recapitulatif</h2>
            %s
          </div>
        </body>
      </html>
      """.formatted(
      safe(order.getPrenom()),
      safe(order.getNom()),
      safe(order.getId()),
      order.getDeliveredAt() == null ? "-" : DATE_FORMATTER.format(order.getDeliveredAt()),
      buildProductsHtml(order)
    );
  }

  private String buildProductsHtml(CustomerOrder order) {
    StringBuilder builder = new StringBuilder();
    for (OrderLine line : order.getLines()) {
      builder.append("""
        <div style="display:flex;gap:16px;align-items:center;padding:12px 0;border-top:1px solid #e5edf6">
          <img src="%s" alt="%s" style="width:80px;height:80px;object-fit:cover;border-radius:14px"/>
          <div>
            <div style="font-weight:700">%s</div>
            <div>Quantite: %d</div>
            <div>Prix unitaire: $%.2f</div>
          </div>
        </div>
        """.formatted(
        safe(line.getProduct().getImageUrl()),
        safe(line.getProduct().getName()),
        safe(line.getProduct().getName()),
        line.getQuantity(),
        line.getProduct().getPrice()
      ));
    }
    return builder.toString();
  }

  private String safe(Object value) {
    return value == null ? "-" : value.toString();
  }
}
