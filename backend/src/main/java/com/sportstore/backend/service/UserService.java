package com.sportstore.backend.service;

import com.sportstore.backend.domain.AppUser;
import com.sportstore.backend.domain.CartLine;
import com.sportstore.backend.domain.Product;
import com.sportstore.backend.domain.Role;
import com.sportstore.backend.dto.StoredCartDto;
import com.sportstore.backend.dto.StoredCartLineDto;
import com.sportstore.backend.dto.UpdateUserRequestDto;
import com.sportstore.backend.dto.UserDto;
import com.sportstore.backend.repository.ProductRepository;
import com.sportstore.backend.repository.UserRepository;
import com.sportstore.backend.security.AuthenticatedUser;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final PasswordEncoder passwordEncoder;
  private final StoreMapper storeMapper;

  public UserService(UserRepository userRepository,
                     ProductRepository productRepository,
                     PasswordEncoder passwordEncoder,
                     StoreMapper storeMapper) {
    this.userRepository = userRepository;
    this.productRepository = productRepository;
    this.passwordEncoder = passwordEncoder;
    this.storeMapper = storeMapper;
  }

  @Transactional(readOnly = true)
  public List<UserDto> getUsers() {
    return userRepository.findAll().stream()
      .map(storeMapper::toUserDto)
      .toList();
  }

  @Transactional
  public UserDto updateUser(Long id, UpdateUserRequestDto request, AuthenticatedUser currentUser) {
    AppUser user = userRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

    if (!currentUser.isAdmin() && !currentUser.getId().equals(id)) {
      throw new ResponseStatusException(FORBIDDEN, "Access denied");
    }

    if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())
      && userRepository.existsByUsername(request.getUsername())) {
      throw new ResponseStatusException(BAD_REQUEST, "Username already exists");
    }

    if (request.getNom() != null) {
      user.setNom(request.getNom());
    }
    if (request.getPrenom() != null) {
      user.setPrenom(request.getPrenom());
    }
    if (request.getAdresse() != null) {
      user.setAdresse(request.getAdresse());
    }
    if (request.getTelephone() != null) {
      user.setTelephone(request.getTelephone());
    }
    if (request.getUsername() != null) {
      user.setUsername(request.getUsername());
    }
    if (request.getPassword() != null && !request.getPassword().isBlank()) {
      user.setPassword(passwordEncoder.encode(request.getPassword()));
    }
    if (currentUser.isAdmin() && request.getRole() != null) {
      user.setRole(Role.valueOf(request.getRole().toUpperCase()));
    }
    if (request.getCart() != null) {
      user.replaceCartLines(toCartLines(request.getCart()));
    }

    return storeMapper.toUserDto(userRepository.save(user));
  }

  private List<CartLine> toCartLines(StoredCartDto cart) {
    List<CartLine> lines = new ArrayList<>();
    if (cart.lines() == null) {
      return lines;
    }

    for (StoredCartLineDto lineDto : cart.lines()) {
      if (lineDto.product() == null || lineDto.product().id() == null) {
        continue;
      }

      Product product = productRepository.findById(lineDto.product().id())
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));

      CartLine line = new CartLine();
      line.setProduct(product);
      line.setQuantity(lineDto.quantity() == null ? 0 : Math.max(lineDto.quantity(), 0));
      lines.add(line);
    }

    return lines;
  }
}
