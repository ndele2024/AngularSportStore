package com.sportstore.backend.controller;

import com.sportstore.backend.dto.OrderDto;
import com.sportstore.backend.security.AuthenticatedUser;
import com.sportstore.backend.service.OrderService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping("/orders")
  public List<OrderDto> getOrders(@AuthenticationPrincipal AuthenticatedUser currentUser) {
    return orderService.getOrders(currentUser);
  }

  @PostMapping("/orders")
  @ResponseStatus(HttpStatus.CREATED)
  public OrderDto createOrder(@RequestBody OrderDto order,
                              @AuthenticationPrincipal AuthenticatedUser currentUser) {
    return orderService.create(order, currentUser);
  }

  @PutMapping("/orders/{id}")
  public OrderDto updateOrder(@PathVariable Long id,
                              @RequestBody OrderDto order,
                              @AuthenticationPrincipal AuthenticatedUser currentUser) {
    return orderService.update(id, order, currentUser);
  }

  @DeleteMapping("/orders/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteOrder(@PathVariable Long id,
                          @AuthenticationPrincipal AuthenticatedUser currentUser) {
    orderService.delete(id, currentUser);
  }
}
