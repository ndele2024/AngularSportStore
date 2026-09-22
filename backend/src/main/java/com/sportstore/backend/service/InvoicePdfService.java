package com.sportstore.backend.service;

import com.sportstore.backend.dto.OrderDto;
import com.sportstore.backend.dto.StoredCartLineDto;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InvoicePdfService {

  private static final DateTimeFormatter DATE_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.of("America/Toronto"));

  public byte[] generate(OrderDto order) {
    List<String> lines = new ArrayList<>();
    lines.add("Facture Sports Store");
    lines.add("Facture: " + safe(order.getInvoiceNumber()));
    lines.add("Commande: #" + safe(order.getId()));
    lines.add("Date: " + formatDate(order.getCreatedAt()));
    lines.add("Client: " + safe(order.getPrenom()) + " " + safe(order.getNom()));
    lines.add("Utilisateur: " + safe(order.getUsername()));
    lines.add("Adresse: " + safe(order.getAdresse()));
    lines.add("Telephone: " + safe(order.getTelephone()));
    lines.add("Statut: " + safe(order.getStatus()));
    lines.add("Paiement: " + safe(order.getPaymentStatus()) + " - " + safe(order.getPaymentReference()));
    lines.add(" ");
    lines.add("Articles");

    if (order.getCart() != null && order.getCart().lines() != null) {
      for (StoredCartLineDto line : order.getCart().lines()) {
        var product = line.product();
        BigDecimal price = product != null && product.price() != null ? product.price() : BigDecimal.ZERO;
        int quantity = line.quantity() == null ? 0 : line.quantity();
        BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(quantity));
        lines.add(String.format("- %s x %d : $%.2f", safe(product != null ? product.name() : "Produit"), quantity, lineTotal));
      }
    }

    lines.add(" ");
    lines.add(String.format("Total: $%.2f", order.getTotal() == null ? BigDecimal.ZERO : order.getTotal()));
    return createPdf(lines);
  }

  private byte[] createPdf(List<String> lines) {
    StringBuilder contentBuilder = new StringBuilder();
    contentBuilder.append("BT\n/F1 12 Tf\n50 790 Td\n14 TL\n");
    for (String line : lines) {
      contentBuilder.append("(").append(escapePdf(line)).append(") Tj\nT*\n");
    }
    contentBuilder.append("ET");
    byte[] contentStream = contentBuilder.toString().getBytes(StandardCharsets.ISO_8859_1);

    List<byte[]> objects = List.of(
      "<< /Type /Catalog /Pages 2 0 R >>".getBytes(StandardCharsets.US_ASCII),
      "<< /Type /Pages /Kids [3 0 R] /Count 1 >>".getBytes(StandardCharsets.US_ASCII),
      "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>"
        .getBytes(StandardCharsets.US_ASCII),
      "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>".getBytes(StandardCharsets.US_ASCII),
      ("<< /Length " + contentStream.length + " >>\nstream\n").getBytes(StandardCharsets.US_ASCII),
      contentStream,
      "\nendstream".getBytes(StandardCharsets.US_ASCII)
    );

    try {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      output.write("%PDF-1.4\n".getBytes(StandardCharsets.US_ASCII));

      List<Integer> offsets = new ArrayList<>();
      offsets.add(0);

      writeObject(output, offsets, 1, objects.get(0));
      writeObject(output, offsets, 2, objects.get(1));
      writeObject(output, offsets, 3, objects.get(2));
      writeObject(output, offsets, 4, objects.get(3));

      offsets.add(output.size());
      output.write("5 0 obj\n".getBytes(StandardCharsets.US_ASCII));
      output.write(objects.get(4));
      output.write(objects.get(5));
      output.write(objects.get(6));
      output.write("\nendobj\n".getBytes(StandardCharsets.US_ASCII));

      int xrefOffset = output.size();
      output.write(("xref\n0 " + offsets.size() + "\n").getBytes(StandardCharsets.US_ASCII));
      output.write("0000000000 65535 f \n".getBytes(StandardCharsets.US_ASCII));
      for (int index = 1; index < offsets.size(); index++) {
        output.write(String.format("%010d 00000 n \n", offsets.get(index)).getBytes(StandardCharsets.US_ASCII));
      }

      output.write(("trailer\n<< /Size " + offsets.size() + " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF")
        .getBytes(StandardCharsets.US_ASCII));

      return output.toByteArray();
    } catch (Exception ex) {
      throw new IllegalStateException("Unable to generate invoice PDF", ex);
    }
  }

  private void writeObject(ByteArrayOutputStream output, List<Integer> offsets, int objectNumber, byte[] body) throws Exception {
    offsets.add(output.size());
    output.write((objectNumber + " 0 obj\n").getBytes(StandardCharsets.US_ASCII));
    output.write(body);
    output.write("\nendobj\n".getBytes(StandardCharsets.US_ASCII));
  }

  private String escapePdf(String value) {
    return safe(value)
      .replace("\\", "\\\\")
      .replace("(", "\\(")
      .replace(")", "\\)");
  }

  private String safe(Object value) {
    return value == null ? "-" : value.toString();
  }

  private String formatDate(java.time.Instant instant) {
    return instant == null ? "-" : DATE_FORMATTER.format(instant);
  }
}
