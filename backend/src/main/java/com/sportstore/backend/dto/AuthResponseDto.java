package com.sportstore.backend.dto;

public record AuthResponseDto(
  boolean success,
  String token,
  UserDto user,
  String message
) {
}
