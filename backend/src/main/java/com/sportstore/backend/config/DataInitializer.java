package com.sportstore.backend.config;

import com.sportstore.backend.domain.AppUser;
import com.sportstore.backend.domain.Product;
import com.sportstore.backend.domain.Role;
import com.sportstore.backend.repository.ProductRepository;
import com.sportstore.backend.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public DataInitializer(ProductRepository productRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {
    seedProducts();
    seedUsers();
  }

  private void seedProducts() {
    if (productRepository.count() > 0) {
      return;
    }

    productRepository.saveAll(List.of(
      product("Kayak", "Watersports", "A boat for one person", "275",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80"),
      product("Lifejacket", "Watersports", "Protective and fashionable", "48.95",
        "https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&w=900&q=80"),
      product("Soccer Ball", "Soccer", "FIFA-approved size and weight", "19.50",
        "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?auto=format&fit=crop&w=900&q=80"),
      product("Corner Flags", "Soccer", "Give your playing field a professional touch", "34.95",
        "https://images.unsplash.com/photo-1547347298-4074fc3086f0?auto=format&fit=crop&w=900&q=80"),
      product("Stadium", "Soccer", "Flat-packed 35,000-seat stadium", "79500",
        "https://images.unsplash.com/photo-1508098682722-e99c643e7485?auto=format&fit=crop&w=900&q=80"),
      product("Thinking Cap", "Chess", "Improve brain efficiency by 75%", "16",
        "https://images.unsplash.com/photo-1528819622765-d6bcf132f793?auto=format&fit=crop&w=900&q=80"),
      product("Unsteady Chair", "Chess", "Secretly give your opponent a disadvantage", "29.95",
        "https://images.unsplash.com/photo-1505842465776-3ac3b43d5d39?auto=format&fit=crop&w=900&q=80"),
      product("Human Chess Board", "Chess", "A fun game for the family", "75",
        "https://images.unsplash.com/photo-1586165368502-1bad197a6461?auto=format&fit=crop&w=900&q=80"),
      product("Bling King", "Chess", "Gold-plated, diamond-studded King", "1200",
        "https://images.unsplash.com/photo-1611195974226-4c4f3cc8e3d6?auto=format&fit=crop&w=900&q=80")
    ));
  }

  private void seedUsers() {
    if (userRepository.count() > 0) {
      return;
    }

    AppUser admin = new AppUser();
    admin.setRole(Role.ADMIN);
    admin.setNom("Admin");
    admin.setPrenom("Super");
    admin.setAdresse("1 Administration Way");
    admin.setTelephone("555-000-0000");
    admin.setUsername("admin");
    admin.setPassword(passwordEncoder.encode("secret"));

    AppUser user = new AppUser();
    user.setRole(Role.USER);
    user.setNom("Doe");
    user.setPrenom("Jane");
    user.setAdresse("25 Market Street");
    user.setTelephone("555-111-2233");
    user.setUsername("jane");
    user.setPassword(passwordEncoder.encode("password"));

    userRepository.saveAll(List.of(admin, user));
  }

  private Product product(String name, String category, String description, String price, String imageUrl) {
    Product product = new Product();
    product.setName(name);
    product.setCategory(category);
    product.setDescription(description);
    product.setPrice(new BigDecimal(price));
    product.setImageUrl(imageUrl);
    return product;
  }
}
