using System.Globalization;
using System.Text;
using SportStore.Api.Dtos;

namespace SportStore.Api.Services;

public class InvoicePdfService
{
    public byte[] Generate(OrderDto order)
    {
        var lines = new List<string>
        {
            "Facture Sports Store",
            $"Facture: {Safe(order.InvoiceNumber)}",
            $"Commande: #{Safe(order.Id)}",
            $"Date: {FormatDate(order.CreatedAt)}",
            $"Client: {Safe(order.Prenom)} {Safe(order.Nom)}",
            $"Utilisateur: {Safe(order.Username)}",
            $"Adresse: {Safe(order.Adresse)}",
            $"Telephone: {Safe(order.Telephone)}",
            $"Statut: {Safe(order.Status)}",
            $"Paiement: {Safe(order.PaymentStatus)} - {Safe(order.PaymentReference)}",
            " ",
            "Articles"
        };

        foreach (var line in order.Cart?.Lines ?? [])
        {
            var price = line.Product?.Price ?? 0m;
            var quantity = line.Quantity ?? 0;
            var total = price * quantity;
            lines.Add($"- {Safe(line.Product?.Name ?? "Produit")} x {quantity} : ${total.ToString("0.00", CultureInfo.InvariantCulture)}");
        }

        lines.Add(" ");
        lines.Add($"Total: ${(order.Total ?? 0m).ToString("0.00", CultureInfo.InvariantCulture)}");
        return CreatePdf(lines);
    }

    private static byte[] CreatePdf(IEnumerable<string> lines)
    {
        var contentBuilder = new StringBuilder();
        contentBuilder.Append("BT\n/F1 12 Tf\n50 790 Td\n14 TL\n");
        foreach (var line in lines)
        {
            contentBuilder.Append('(').Append(EscapePdf(line)).Append(") Tj\nT*\n");
        }
        contentBuilder.Append("ET");

        var contentBytes = Encoding.ASCII.GetBytes(contentBuilder.ToString());
        var objects = new List<byte[]>
        {
            Encoding.ASCII.GetBytes("<< /Type /Catalog /Pages 2 0 R >>"),
            Encoding.ASCII.GetBytes("<< /Type /Pages /Kids [3 0 R] /Count 1 >>"),
            Encoding.ASCII.GetBytes("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>"),
            Encoding.ASCII.GetBytes("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>")
        };

        using var stream = new MemoryStream();
        stream.Write(Encoding.ASCII.GetBytes("%PDF-1.4\n"));

        var offsets = new List<int> { 0 };
        WriteObject(stream, offsets, 1, objects[0]);
        WriteObject(stream, offsets, 2, objects[1]);
        WriteObject(stream, offsets, 3, objects[2]);
        WriteObject(stream, offsets, 4, objects[3]);

        offsets.Add((int)stream.Position);
        stream.Write(Encoding.ASCII.GetBytes("5 0 obj\n"));
        stream.Write(Encoding.ASCII.GetBytes($"<< /Length {contentBytes.Length} >>\nstream\n"));
        stream.Write(contentBytes);
        stream.Write(Encoding.ASCII.GetBytes("\nendstream\nendobj\n"));

        var xrefOffset = (int)stream.Position;
        stream.Write(Encoding.ASCII.GetBytes($"xref\n0 {offsets.Count}\n"));
        stream.Write(Encoding.ASCII.GetBytes("0000000000 65535 f \n"));
        foreach (var offset in offsets.Skip(1))
        {
            stream.Write(Encoding.ASCII.GetBytes($"{offset:0000000000} 00000 n \n"));
        }

        stream.Write(Encoding.ASCII.GetBytes($"trailer\n<< /Size {offsets.Count} /Root 1 0 R >>\nstartxref\n{xrefOffset}\n%%EOF"));
        return stream.ToArray();
    }

    private static void WriteObject(Stream stream, List<int> offsets, int number, byte[] body)
    {
        offsets.Add((int)stream.Position);
        stream.Write(Encoding.ASCII.GetBytes($"{number} 0 obj\n"));
        stream.Write(body);
        stream.Write(Encoding.ASCII.GetBytes("\nendobj\n"));
    }

    private static string EscapePdf(string value) =>
        value.Replace("\\", "\\\\").Replace("(", "\\(").Replace(")", "\\)");

    private static string FormatDate(DateTime? value) =>
        value?.ToLocalTime().ToString("yyyy-MM-dd HH:mm", CultureInfo.InvariantCulture) ?? "-";

    private static string Safe(object? value) => value?.ToString() ?? "-";
}
