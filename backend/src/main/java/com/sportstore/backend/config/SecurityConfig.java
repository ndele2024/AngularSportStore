package com.sportstore.backend.config;

import com.sportstore.backend.security.JwtAuthenticationFilter;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  /**
   * Origines acceptees, separees par des virgules. En production, le frontend et
   * l'API partagent le sous-domaine : il n'y a donc aucune requete croisee. Mais
   * les navigateurs envoient tout de meme l'en-tete « Origin » sur les requetes
   * POST, PUT et DELETE de meme origine — si elle ne figure pas ici, Spring
   * Security repond 403 et la connexion, les commandes et le paiement echouent.
   */
  private final List<String> allowedOrigins;

  public SecurityConfig(
      JwtAuthenticationFilter jwtAuthenticationFilter,
      @Value("${app.cors.allowed-origins}") String allowedOrigins) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
        .map(String::trim)
        .filter(origine -> !origine.isEmpty())
        .toList();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(Customizer.withDefaults())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/login", "/register", "/error").permitAll()
        // Interroge par le healthcheck Docker, jamais expose publiquement :
        // nginx ne relaie pas /api/actuator vers l'exterieur.
        .requestMatchers("/actuator/health").permitAll()
        .requestMatchers(HttpMethod.GET, "/products").permitAll()
        .requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(allowedOrigins);
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
