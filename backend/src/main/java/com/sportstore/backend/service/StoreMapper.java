package com.sportstore.backend.service;

import com.sportstore.backend.domain.AppUser;
import com.sportstore.backend.domain.CartLine;
import com.sportstore.backend.domain.CustomerOrder;
import com.sportstore.backend.domain.OrderLine;
import com.sportstore.backend.domain.Product;
import com.sportstore.backend.dto.OrderDto;
import com.sportstore.backend.dto.ProductDto;
import com.sportstore.backend.dto.StoredCartDto;
import com.sportstore.backend.dto.StoredCartLineDto;
import com.sportstore.backend.dto.UserDto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StoreMapper {

  public ProductDto toProductDto(Product product) {
    return new ProductDto(
      product.getId(),
      product.getName(),
      product.getCategory(),
      product.getDescription(),
      product.getPrice(),
      product.getImageUrl()
    );
  }

  public UserDto toUserDto(AppUser user) {
    return new UserDto(
      user.getId(),
      user.getRole().name().toLowerCase(),
      user.getNom(),
      user.getPrenom(),
      user.getAdresse(),
      user.getTelephone(),
      user.getUsername(),
      toStoredCartFromCartLines(user.getCartLines())
    );
  }

  public OrderDto toOrderDto(CustomerOrder order) {
    OrderDto dto = new OrderDto();
    dto.setId(order.getId());
    dto.setUserId(order.getUserId());
    dto.setUsername(order.getUsername());
    dto.setNom(order.getNom());
    dto.setPrenom(order.getPrenom());
    dto.setAdresse(order.getAdresse());
    dto.setTelephone(order.getTelephone());
    dto.setCreatedAt(order.getCreatedAt());
    dto.setTotal(order.getTotal());
    dto.setItemCount(order.getItemCount());
    dto.setShipped(order.isShipped());
    dto.setCart(toStoredCartFromOrderLines(order.getLines()));
    return dto;
  }

  public StoredCartDto toStoredCartFromCartLines(List<CartLine> lines) {
    List<StoredCartLineDto> dtos = new ArrayList<>();
    int itemCount = 0;
    BigDecimal total = BigDecimal.ZERO;

    for (CartLine line : lines) {
      dtos.add(new StoredCartLineDto(toProductDto(line.getProduct()), line.getQuantity()));
      itemCount += safeQuantity(line.getQuantity());
      total = total.add(line.getProduct().getPrice().multiply(BigDecimal.valueOf(safeQuantity(line.getQuantity()))));
    }

    return new StoredCartDto(dtos, itemCount, total);
  }

  public StoredCartDto toStoredCartFromOrderLines(List<OrderLine> lines) {
    List<StoredCartLineDto> dtos = new ArrayList<>();
    int itemCount = 0;
    BigDecimal total = BigDecimal.ZERO;

    for (OrderLine line : lines) {
      dtos.add(new StoredCartLineDto(toProductDto(line.getProduct()), line.getQuantity()));
      itemCount += safeQuantity(line.getQuantity());
      total = total.add(line.getProduct().getPrice().multiply(BigDecimal.valueOf(safeQuantity(line.getQuantity()))));
    }

    return new StoredCartDto(dtos, itemCount, total);
  }

  private int safeQuantity(Integer quantity) {
    return quantity == null ? 0 : Math.max(quantity, 0);
  }
}
