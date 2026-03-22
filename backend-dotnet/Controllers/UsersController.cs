using BCrypt.Net;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SportStore.Api.Data;
using SportStore.Api.Dtos;
using SportStore.Api.Extensions;
using SportStore.Api.Models;
using SportStore.Api.Services;

namespace SportStore.Api.Controllers;

[ApiController]
[Authorize]
public class UsersController(SportStoreDbContext dbContext, StoreMapper mapper) : ControllerBase
{
    [HttpGet("users")]
    [Authorize(Roles = "Admin")]
    public async Task<ActionResult<IEnumerable<UserDto>>> GetUsers()
    {
        var users = await dbContext.Users
            .Include(u => u.CartLines)
            .ThenInclude(c => c.Product)
            .ToListAsync();

        return Ok(users.Select(mapper.ToUserDto));
    }

    [HttpPatch("users/{id:long}")]
    public async Task<ActionResult<UserDto>> UpdateUser(long id, [FromBody] UpdateUserRequestDto request)
    {
        var currentUserId = User.GetUserId();
        var isAdmin = User.IsAdmin();

        if (!isAdmin && currentUserId != id)
        {
            return StatusCode(StatusCodes.Status403Forbidden, new ErrorResponseDto(false, "Access denied"));
        }

        var user = await dbContext.Users
            .Include(u => u.CartLines)
            .ThenInclude(line => line.Product)
            .FirstOrDefaultAsync(u => u.Id == id);

        if (user is null)
        {
            return NotFound(new ErrorResponseDto(false, "User not found"));
        }

        if (!string.IsNullOrWhiteSpace(request.Username) && request.Username != user.Username)
        {
            var exists = await dbContext.Users.AnyAsync(u => u.Username == request.Username && u.Id != id);
            if (exists)
            {
                return BadRequest(new ErrorResponseDto(false, "Username already exists"));
            }
            user.Username = request.Username;
        }

        if (!string.IsNullOrWhiteSpace(request.Nom)) user.Nom = request.Nom;
        if (!string.IsNullOrWhiteSpace(request.Prenom)) user.Prenom = request.Prenom;
        if (!string.IsNullOrWhiteSpace(request.Adresse)) user.Adresse = request.Adresse;
        if (!string.IsNullOrWhiteSpace(request.Telephone)) user.Telephone = request.Telephone;
        if (!string.IsNullOrWhiteSpace(request.Password)) user.PasswordHash = BCrypt.Net.BCrypt.HashPassword(request.Password);
        if (isAdmin && !string.IsNullOrWhiteSpace(request.Role) && Enum.TryParse<Role>(request.Role, true, out var parsedRole))
        {
            user.Role = parsedRole;
        }

        if (request.Cart is not null)
        {
            dbContext.CartLines.RemoveRange(user.CartLines);
            user.CartLines.Clear();

            foreach (var lineDto in request.Cart.Lines ?? [])
            {
                if (lineDto.Product?.Id is null) continue;

                var product = await dbContext.Products.FindAsync(lineDto.Product.Id.Value);
                if (product is null) continue;

                user.CartLines.Add(new CartLine
                {
                    User = user,
                    Product = product,
                    Quantity = Math.Max(lineDto.Quantity ?? 0, 0)
                });
            }
        }

        await dbContext.SaveChangesAsync();

        var updatedUser = await dbContext.Users
            .Include(u => u.CartLines)
            .ThenInclude(line => line.Product)
            .FirstAsync(u => u.Id == id);

        return Ok(mapper.ToUserDto(updatedUser));
    }
}
