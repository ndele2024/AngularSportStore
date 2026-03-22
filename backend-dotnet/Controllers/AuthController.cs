using BCrypt.Net;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SportStore.Api.Data;
using SportStore.Api.Dtos;
using SportStore.Api.Models;
using SportStore.Api.Services;

namespace SportStore.Api.Controllers;

[ApiController]
public class AuthController(SportStoreDbContext dbContext, JwtTokenService jwtTokenService, StoreMapper mapper) : ControllerBase
{
    [HttpPost("login")]
    public async Task<ActionResult<AuthResponseDto>> Login([FromBody] LoginRequestDto request)
    {
        var user = await dbContext.Users
            .Include(u => u.CartLines)
            .ThenInclude(line => line.Product)
            .FirstOrDefaultAsync(u => u.Username == request.Username);

        if (user is null || !BCrypt.Net.BCrypt.Verify(request.Password, user.PasswordHash))
        {
            return Ok(new AuthResponseDto(false, null, null, "Invalid username or password"));
        }

        return Ok(new AuthResponseDto(true, jwtTokenService.CreateToken(user), mapper.ToUserDto(user), null));
    }

    [HttpPost("register")]
    public async Task<ActionResult<AuthResponseDto>> Register([FromBody] RegisterRequestDto request)
    {
        if (string.IsNullOrWhiteSpace(request.Nom) || string.IsNullOrWhiteSpace(request.Prenom) ||
            string.IsNullOrWhiteSpace(request.Adresse) || string.IsNullOrWhiteSpace(request.Telephone) ||
            string.IsNullOrWhiteSpace(request.Username) || string.IsNullOrWhiteSpace(request.Password))
        {
            return BadRequest(new ErrorResponseDto(false, "Missing required fields"));
        }

        var exists = await dbContext.Users.AnyAsync(u => u.Username == request.Username);
        if (exists)
        {
            return BadRequest(new ErrorResponseDto(false, "Username already exists"));
        }

        var user = new AppUser
        {
            Role = Role.User,
            Nom = request.Nom.Trim(),
            Prenom = request.Prenom.Trim(),
            Adresse = request.Adresse.Trim(),
            Telephone = request.Telephone.Trim(),
            Username = request.Username.Trim(),
            PasswordHash = BCrypt.Net.BCrypt.HashPassword(request.Password)
        };

        dbContext.Users.Add(user);
        await dbContext.SaveChangesAsync();

        return StatusCode(StatusCodes.Status201Created, new AuthResponseDto(true, jwtTokenService.CreateToken(user), mapper.ToUserDto(user), null));
    }
}
