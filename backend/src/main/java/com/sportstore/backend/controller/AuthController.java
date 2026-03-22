package com.sportstore.backend.controller;

import com.sportstore.backend.dto.AuthResponseDto;
import com.sportstore.backend.dto.LoginRequestDto;
import com.sportstore.backend.dto.RegisterRequestDto;
import com.sportstore.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public AuthResponseDto login(@RequestBody LoginRequestDto request) {
    return authService.login(request);
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponseDto register(@RequestBody RegisterRequestDto request) {
    return authService.register(request);
  }
}
