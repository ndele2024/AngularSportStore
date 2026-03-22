using System.Security.Claims;

namespace SportStore.Api.Extensions;

public static class ClaimsPrincipalExtensions
{
    public static long GetUserId(this ClaimsPrincipal principal)
    {
        var value = principal.FindFirst("userId")?.Value;
        return long.TryParse(value, out var id) ? id : 0;
    }

    public static bool IsAdmin(this ClaimsPrincipal principal) =>
        string.Equals(principal.FindFirst(ClaimTypes.Role)?.Value, "Admin", StringComparison.OrdinalIgnoreCase);
}
