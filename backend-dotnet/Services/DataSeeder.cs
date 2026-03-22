using BCrypt.Net;
using Microsoft.EntityFrameworkCore;
using SportStore.Api.Data;
using SportStore.Api.Models;

namespace SportStore.Api.Services;

public class DataSeeder(SportStoreDbContext dbContext)
{
    public async Task SeedAsync()
    {
        if (!await dbContext.Products.AnyAsync())
        {
            dbContext.Products.AddRange(
                Product("Kayak", "Watersports", "A boat for one person", 275m, "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80"),
                Product("Lifejacket", "Watersports", "Protective and fashionable", 48.95m, "https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&w=900&q=80"),
                Product("Soccer Ball", "Soccer", "FIFA-approved size and weight", 19.50m, "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?auto=format&fit=crop&w=900&q=80"),
                Product("Corner Flags", "Soccer", "Give your playing field a professional touch", 34.95m, "https://images.unsplash.com/photo-1547347298-4074fc3086f0?auto=format&fit=crop&w=900&q=80"),
                Product("Stadium", "Soccer", "Flat-packed 35,000-seat stadium", 79500m, "https://images.unsplash.com/photo-1508098682722-e99c643e7485?auto=format&fit=crop&w=900&q=80"),
                Product("Thinking Cap", "Chess", "Improve brain efficiency by 75%", 16m, "https://images.unsplash.com/photo-1528819622765-d6bcf132f793?auto=format&fit=crop&w=900&q=80"),
                Product("Unsteady Chair", "Chess", "Secretly give your opponent a disadvantage", 29.95m, "https://images.unsplash.com/photo-1505842465776-3ac3b43d5d39?auto=format&fit=crop&w=900&q=80"),
                Product("Human Chess Board", "Chess", "A fun game for the family", 75m, "https://images.unsplash.com/photo-1586165368502-1bad197a6461?auto=format&fit=crop&w=900&q=80"),
                Product("Bling King", "Chess", "Gold-plated, diamond-studded King", 1200m, "https://images.unsplash.com/photo-1611195974226-4c4f3cc8e3d6?auto=format&fit=crop&w=900&q=80")
            );
        }

        if (!await dbContext.Users.AnyAsync())
        {
            dbContext.Users.AddRange(
                new AppUser
                {
                    Role = Role.Admin,
                    Nom = "Admin",
                    Prenom = "Super",
                    Adresse = "1 Administration Way",
                    Telephone = "555-000-0000",
                    Username = "admin",
                    PasswordHash = BCrypt.Net.BCrypt.HashPassword("secret")
                },
                new AppUser
                {
                    Role = Role.User,
                    Nom = "Doe",
                    Prenom = "Jane",
                    Adresse = "25 Market Street",
                    Telephone = "555-111-2233",
                    Username = "jane",
                    PasswordHash = BCrypt.Net.BCrypt.HashPassword("password")
                }
            );
        }

        await dbContext.SaveChangesAsync();
    }

    private static Product Product(string name, string category, string description, decimal price, string imageUrl) =>
        new()
        {
            Name = name,
            Category = category,
            Description = description,
            Price = price,
            ImageUrl = imageUrl
        };
}
