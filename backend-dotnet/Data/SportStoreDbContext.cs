using Microsoft.EntityFrameworkCore;
using SportStore.Api.Models;

namespace SportStore.Api.Data;

public class SportStoreDbContext(DbContextOptions<SportStoreDbContext> options) : DbContext(options)
{
    public DbSet<Product> Products => Set<Product>();
    public DbSet<AppUser> Users => Set<AppUser>();
    public DbSet<CartLine> CartLines => Set<CartLine>();
    public DbSet<CustomerOrder> Orders => Set<CustomerOrder>();
    public DbSet<OrderLine> OrderLines => Set<OrderLine>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<AppUser>()
            .HasIndex(u => u.Username)
            .IsUnique();

        modelBuilder.Entity<AppUser>()
            .Property(u => u.Role)
            .HasConversion<string>();

        modelBuilder.Entity<Product>()
            .Property(p => p.Price)
            .HasPrecision(12, 2);

        modelBuilder.Entity<CustomerOrder>()
            .Property(o => o.Total)
            .HasPrecision(12, 2);

        modelBuilder.Entity<AppUser>()
            .HasMany(u => u.CartLines)
            .WithOne(c => c.User)
            .HasForeignKey(c => c.UserId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<CartLine>()
            .HasOne(c => c.Product)
            .WithMany()
            .HasForeignKey(c => c.ProductId)
            .OnDelete(DeleteBehavior.Restrict);

        modelBuilder.Entity<CustomerOrder>()
            .HasMany(o => o.Lines)
            .WithOne(l => l.Order)
            .HasForeignKey(l => l.OrderId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<OrderLine>()
            .HasOne(l => l.Product)
            .WithMany()
            .HasForeignKey(l => l.ProductId)
            .OnDelete(DeleteBehavior.Restrict);
    }
}
