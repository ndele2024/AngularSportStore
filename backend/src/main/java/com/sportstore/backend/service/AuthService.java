package com.sportstore.backend.service;

import com.sportstore.backend.domain.AppUser;
import com.sportstore.backend.domain.Role;
import com.sportstore.backend.dto.AuthResponseDto;
import com.sportstore.backend.dto.LoginRequestDto;
import com.sportstore.backend.dto.RegisterRequestDto;
import com.sportstore.backend.repository.UserRepository;
import com.sportstore.backend.security.AuthenticatedUser;
import com.sportstore.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final StoreMapper storeMapper;

  public AuthService(UserRepository userRepository,
                     PasswordEncoder passwordEncoder,
                     JwtService jwtService,
                     StoreMapper storeMapper) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.storeMapper = storeMapper;
  }

  @Transactional(readOnly = true)
  public AuthResponseDto login(LoginRequestDto request) {
    AppUser user = userRepository.findByUsername(request.getUsername())
      .orElse(null);

    if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      return new AuthResponseDto(false, null, null, "Invalid username or password");
    }

    AuthenticatedUser principal = new AuthenticatedUser(
      user.getId(),
      user.getUsername(),
      user.getPassword(),
      user.getRole()
    );

    return new AuthResponseDto(true, jwtService.generateToken(principal), storeMapper.toUserDto(user), null);
  }

  @Transactional
  public AuthResponseDto register(RegisterRequestDto request) {
    validateRegistration(request);

    AppUser user = new AppUser();
    user.setRole(Role.USER);
    user.setNom(request.getNom().trim());
    user.setPrenom(request.getPrenom().trim());
    user.setAdresse(request.getAdresse().trim());
    user.setTelephone(request.getTelephone().trim());
    user.setUsername(request.getUsername().trim());
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    AppUser saved = userRepository.save(user);
    AuthenticatedUser principal = new AuthenticatedUser(
      saved.getId(),
      saved.getUsername(),
      saved.getPassword(),
      saved.getRole()
    );

    return new AuthResponseDto(true, jwtService.generateToken(principal), storeMapper.toUserDto(saved), null);
  }

  private void validateRegistration(RegisterRequestDto request) {
    if (isBlank(request.getNom()) || isBlank(request.getPrenom()) || isBlank(request.getAdresse())
      || isBlank(request.getTelephone()) || isBlank(request.getUsername()) || isBlank(request.getPassword())) {
      throw new ResponseStatusException(BAD_REQUEST, "Missing required fields");
    }

    if (userRepository.existsByUsername(request.getUsername().trim())) {
      throw new ResponseStatusException(BAD_REQUEST, "Username already exists");
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
