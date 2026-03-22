package com.sportstore.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final CustomUserDetailsService userDetailsService;

  public JwtAuthenticationFilter(JwtService jwtService,
                                 CustomUserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    String token = extractToken(request.getHeader("Authorization"));

    if (token == null || SecurityContextHolder.getContext().getAuthentication() != null) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String username = jwtService.extractUsername(token);
      AuthenticatedUser user = (AuthenticatedUser) userDetailsService.loadUserByUsername(username);

      if (jwtService.isTokenValid(token, user)) {
        UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception ignored) {
    }

    filterChain.doFilter(request, response);
  }

  private String extractToken(String header) {
    if (header == null || header.isBlank()) {
      return null;
    }
    if (header.startsWith("Bearer<") && header.endsWith(">")) {
      return header.substring(7, header.length() - 1);
    }
    if (header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }
}
