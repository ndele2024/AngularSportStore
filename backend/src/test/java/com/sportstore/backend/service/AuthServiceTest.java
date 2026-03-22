package com.sportstore.backend.service;

import com.sportstore.backend.domain.AppUser;
import com.sportstore.backend.domain.Role;
import com.sportstore.backend.dto.AuthResponseDto;
import com.sportstore.backend.dto.LoginRequestDto;
import com.sportstore.backend.dto.RegisterRequestDto;
import com.sportstore.backend.dto.StoredCartDto;
import com.sportstore.backend.dto.UserDto;
import com.sportstore.backend.repository.UserRepository;
import com.sportstore.backend.security.AuthenticatedUser;
import com.sportstore.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private JwtService jwtService;
  @Mock
  private StoreMapper storeMapper;

  @InjectMocks
  private AuthService authService;

  private AppUser user;

  @BeforeEach
  void setUp() {
    user = new AppUser();
    user.setId(1L);
    user.setRole(Role.USER);
    user.setNom("Doe");
    user.setPrenom("Jane");
    user.setAdresse("25 Market Street");
    user.setTelephone("555-111-2233");
    user.setUsername("jane");
    user.setPassword("encoded");
  }

  @Test
  void loginReturnsSuccessWhenCredentialsAreValid() {
    LoginRequestDto request = new LoginRequestDto();
    request.setUsername("jane");
    request.setPassword("password");

    when(userRepository.findByUsername("jane")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
    when(jwtService.generateToken(any(AuthenticatedUser.class))).thenReturn("jwt-token");
    when(storeMapper.toUserDto(user)).thenReturn(new UserDto(1L, "user", "Doe", "Jane", "25 Market Street", "555", "jane",
      new StoredCartDto(java.util.List.of(), 0, java.math.BigDecimal.ZERO)));

    AuthResponseDto response = authService.login(request);

    assertTrue(response.success());
    assertEquals("jwt-token", response.token());
    assertNotNull(response.user());
  }

  @Test
  void loginReturnsFailureWhenPasswordIsInvalid() {
    LoginRequestDto request = new LoginRequestDto();
    request.setUsername("jane");
    request.setPassword("wrong");

    when(userRepository.findByUsername("jane")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

    AuthResponseDto response = authService.login(request);

    assertFalse(response.success());
    assertEquals("Invalid username or password", response.message());
    verify(jwtService, never()).generateToken(any());
  }

  @Test
  void registerThrowsWhenUsernameAlreadyExists() {
    RegisterRequestDto request = new RegisterRequestDto();
    request.setNom("Doe");
    request.setPrenom("Jane");
    request.setAdresse("25 Market Street");
    request.setTelephone("555");
    request.setUsername("jane");
    request.setPassword("password");

    when(userRepository.existsByUsername("jane")).thenReturn(true);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
      () -> authService.register(request));

    assertEquals("400 BAD_REQUEST \"Username already exists\"", exception.getMessage());
  }

  @Test
  void registerEncodesPasswordAndSavesUser() {
    RegisterRequestDto request = new RegisterRequestDto();
    request.setNom("Doe");
    request.setPrenom("Jane");
    request.setAdresse("25 Market Street");
    request.setTelephone("555");
    request.setUsername("jane");
    request.setPassword("password");

    when(userRepository.existsByUsername("jane")).thenReturn(false);
    when(passwordEncoder.encode("password")).thenReturn("encoded-password");
    when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
      AppUser saved = invocation.getArgument(0);
      saved.setId(2L);
      return saved;
    });
    when(jwtService.generateToken(any(AuthenticatedUser.class))).thenReturn("jwt-token");
    when(storeMapper.toUserDto(any(AppUser.class))).thenReturn(new UserDto(2L, "user", "Doe", "Jane", "25 Market Street", "555", "jane",
      new StoredCartDto(java.util.List.of(), 0, java.math.BigDecimal.ZERO)));

    AuthResponseDto response = authService.register(request);

    ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
    verify(userRepository).save(userCaptor.capture());
    assertEquals("encoded-password", userCaptor.getValue().getPassword());
    assertTrue(response.success());
  }
}
