package com.sportstore.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecretKey signingKey;

  public JwtService(@Value("${app.jwt.secret}") String secret) {
    byte[] keyBytes = secret.length() >= 32
      ? secret.getBytes(StandardCharsets.UTF_8)
      : Decoders.BASE64.decode("c3BvcnQtc3RvcmUtZGV2LXNlY3JldC1rZXktZm9yLWp3dC10b2tlbi0xMjM0NTY=");
    this.signingKey = Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(AuthenticatedUser user) {
    Instant now = Instant.now();
    return Jwts.builder()
      .subject(user.getUsername())
      .claim("userId", user.getId())
      .claim("role", user.getRole().name())
      .issuedAt(Date.from(now))
      .expiration(Date.from(now.plusSeconds(8 * 60 * 60)))
      .signWith(signingKey)
      .compact();
  }

  public String extractUsername(String token) {
    return parseClaims(token).getSubject();
  }

  public boolean isTokenValid(String token, AuthenticatedUser user) {
    Claims claims = parseClaims(token);
    return claims.getSubject().equals(user.getUsername())
      && claims.getExpiration().after(new Date());
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
      .verifyWith(signingKey)
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }
}
