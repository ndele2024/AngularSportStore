using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using SportStore.Api.Data;
using SportStore.Api.Options;
using SportStore.Api.Services;

var builder = WebApplication.CreateBuilder(args);
builder.WebHost.UseUrls("http://localhost:3500");

builder.Services.Configure<JwtOptions>(builder.Configuration.GetSection("Jwt"));

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddDbContext<SportStoreDbContext>(options =>
{
    var provider = builder.Configuration["DatabaseProvider"];
    if (string.Equals(provider, "InMemory", StringComparison.OrdinalIgnoreCase))
    {
        var databaseName = builder.Configuration["InMemoryDatabaseName"] ?? "SportStoreTests";
        options.UseInMemoryDatabase(databaseName);
    }
    else
    {
        options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection"));
    }
});

builder.Services.AddScoped<JwtTokenService>();
builder.Services.AddScoped<StoreMapper>();
builder.Services.AddScoped<DataSeeder>();

var jwtOptions = builder.Configuration.GetSection("Jwt").Get<JwtOptions>() ?? new JwtOptions();
var signingKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtOptions.Secret));

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.Events = new JwtBearerEvents
        {
            OnMessageReceived = context =>
            {
                var header = context.Request.Headers.Authorization.ToString();
                if (header.StartsWith("Bearer<") && header.EndsWith(">"))
                {
                    context.Token = header[7..^1];
                }
                else if (header.StartsWith("Bearer "))
                {
                    context.Token = header[7..];
                }

                return Task.CompletedTask;
            }
        };

        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            ValidIssuer = jwtOptions.Issuer,
            ValidAudience = jwtOptions.Audience,
            IssuerSigningKey = signingKey
        };
    });

builder.Services.AddAuthorization();

builder.Services.AddCors(options =>
{
    options.AddPolicy("Frontend", policy =>
    {
        policy
            .SetIsOriginAllowed(origin => origin.StartsWith("http://localhost:") || origin.StartsWith("http://127.0.0.1:"))
            .AllowAnyHeader()
            .AllowAnyMethod()
            .AllowCredentials();
    });
});

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors("Frontend");
app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<SportStoreDbContext>();
    db.Database.EnsureCreated();

    var seeder = scope.ServiceProvider.GetRequiredService<DataSeeder>();
    await seeder.SeedAsync();
}

app.Run();

public partial class Program
{
}
