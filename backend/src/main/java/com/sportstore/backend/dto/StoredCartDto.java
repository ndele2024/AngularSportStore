package com.sportstore.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record StoredCartDto(
  List<StoredCartLineDto> lines,
  Integer itemCount,
  BigDecimal cartPrice
) {
}
