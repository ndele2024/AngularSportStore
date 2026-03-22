package com.sportstore.backend.dto;

public record StoredCartLineDto(
  ProductDto product,
  Integer quantity
) {
}
