package com.sportstore.backend.controller;

import com.sportstore.backend.dto.UpdateUserRequestDto;
import com.sportstore.backend.dto.UserDto;
import com.sportstore.backend.security.AuthenticatedUser;
import com.sportstore.backend.service.UserService;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  public List<UserDto> getUsers() {
    return userService.getUsers();
  }

  @PatchMapping("/users/{id}")
  public UserDto updateUser(@PathVariable Long id,
                            @RequestBody UpdateUserRequestDto request,
                            @AuthenticationPrincipal AuthenticatedUser currentUser) {
    return userService.updateUser(id, request, currentUser);
  }
}
