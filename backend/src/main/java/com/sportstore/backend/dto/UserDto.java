package com.sportstore.backend.dto;

public record UserDto(
  Long id,
  String role,
  String nom,
  String prenom,
  String adresse,
  String telephone,
  String username,
  StoredCartDto cart
) {
}
