package com.sportstore.backend.service;

import com.sportstore.backend.domain.CustomerOrder;
import com.sportstore.backend.domain.OrderLine;
import com.sportstore.backend.domain.Product;
import com.sportstore.backend.dto.OrderDto;
import com.sportstore.backend.dto.StoredCartDto;
import com.sportstore.backend.dto.StoredCartLineDto;
import com.sportstore.backend.repository.OrderRepository;
import com.sportstore.backend.repository.ProductRepository;
import com.sportstore.backend.repository.UserRepository;
import com.sportstore.backend.security.AuthenticatedUser;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final StoreMapper storeMapper;
  private final InvoicePdfService invoicePdfService;
  private final OrderNotificationService orderNotificationService;

  public OrderService(OrderRepository orderRepository,
                      ProductRepository productRepository,
                      UserRepository userRepository,
                      StoreMapper storeMapper,
                      InvoicePdfService invoicePdfService,
                      OrderNotificationService orderNotificationService) {
    this.orderRepository = orderRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.storeMapper = storeMapper;
    this.invoicePdfService = invoicePdfService;
    this.orderNotificationService = orderNotificationService;
  }

  @Transactional(readOnly = true)
  public List<OrderDto> getOrders(AuthenticatedUser currentUser) {
    List<CustomerOrder> orders = currentUser.isAdmin()
      ? orderRepository.findAllByOrderByCreatedAtDesc()
      : orderRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
    return orders.stream().map(storeMapper::toOrderDto).toList();
  }

  @Transactional
  public OrderDto create(OrderDto request, AuthenticatedUser currentUser) {
    var user = userRepository.findById(currentUser.getId())
      .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

    List<OrderLine> lines = toOrderLines(request.getCart());
    OrderTotals totals = computeTotals(lines);

    CustomerOrder order = new CustomerOrder();
    order.setUserId(user.getId());
    order.setUsername(user.getUsername());
    order.setNom(request.getNom() != null ? request.getNom() : user.getNom());
    order.setPrenom(request.getPrenom() != null ? request.getPrenom() : user.getPrenom());
    order.setAdresse(request.getAdresse() != null ? request.getAdresse() : user.getAdresse());
    order.setTelephone(request.getTelephone() != null ? request.getTelephone() : user.getTelephone());
    order.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : Instant.now());
    order.setItemCount(totals.itemCount());
    order.setTotal(totals.total());
    order.setStatus("EN_TRAITEMENT");
    order.setPaymentStatus(defaultValue(request.getPaymentStatus(), "PAYE"));
    order.setPaymentMethod(defaultValue(request.getPaymentMethod(), "CARTE_CREDIT"));
    order.setPaymentReference(defaultValue(request.getPaymentReference(), generatePaymentReference()));
    order.setPaymentLast4(defaultValue(request.getPaymentLast4(), "0000"));
    order.setInvoiceNumber(generateInvoiceNumber(user.getId()));
    order.setDeliveredAt(null);
    order.setShipped(false);
    order.replaceLines(lines);

    CustomerOrder savedOrder = orderRepository.save(order);
    orderNotificationService.sendOrderConfirmation(savedOrder);
    return storeMapper.toOrderDto(savedOrder);
  }

  @Transactional
  public OrderDto update(Long id, OrderDto request, AuthenticatedUser currentUser) {
    CustomerOrder order = getOrderEntity(id, currentUser);

    if (!currentUser.isAdmin()) {
      throw new ResponseStatusException(FORBIDDEN, "Admin access required");
    }

    if (request.getNom() != null) {
      order.setNom(request.getNom());
    }
    if (request.getPrenom() != null) {
      order.setPrenom(request.getPrenom());
    }
    if (request.getAdresse() != null) {
      order.setAdresse(request.getAdresse());
    }
    if (request.getTelephone() != null) {
      order.setTelephone(request.getTelephone());
    }

    boolean wasDelivered = "LIVRE".equals(order.getStatus());
    String nextStatus = request.getStatus() != null
      ? request.getStatus()
      : (request.isShipped() ? "LIVRE" : "EN_TRAITEMENT");

    order.setStatus(nextStatus);
    order.setShipped("LIVRE".equals(nextStatus));
    if (order.isShipped()) {
      order.setDeliveredAt(order.getDeliveredAt() == null ? Instant.now() : order.getDeliveredAt());
    } else {
      order.setDeliveredAt(null);
    }

    if (request.getPaymentStatus() != null) {
      order.setPaymentStatus(request.getPaymentStatus());
    }
    if (request.getPaymentMethod() != null) {
      order.setPaymentMethod(request.getPaymentMethod());
    }
    if (request.getPaymentReference() != null) {
      order.setPaymentReference(request.getPaymentReference());
    }
    if (request.getPaymentLast4() != null) {
      order.setPaymentLast4(request.getPaymentLast4());
    }

    if (request.getCart() != null) {
      List<OrderLine> lines = toOrderLines(request.getCart());
      OrderTotals totals = computeTotals(lines);
      order.replaceLines(lines);
      order.setItemCount(totals.itemCount());
      order.setTotal(totals.total());
    }

    CustomerOrder savedOrder = orderRepository.save(order);
    if (!wasDelivered && "LIVRE".equals(savedOrder.getStatus())) {
      orderNotificationService.sendDeliveryConfirmation(savedOrder);
    }
    return storeMapper.toOrderDto(savedOrder);
  }

  @Transactional
  public void delete(Long id, AuthenticatedUser currentUser) {
    CustomerOrder order = orderRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"));

    if (!currentUser.isAdmin()) {
      throw new ResponseStatusException(FORBIDDEN, "Admin access required");
    }

    orderRepository.delete(order);
  }

  @Transactional(readOnly = true)
  public byte[] generateInvoice(Long id, AuthenticatedUser currentUser) {
    CustomerOrder order = getOrderEntity(id, currentUser);
    return invoicePdfService.generate(storeMapper.toOrderDto(order));
  }

  private CustomerOrder getOrderEntity(Long id, AuthenticatedUser currentUser) {
    CustomerOrder order = orderRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"));

    if (!currentUser.isAdmin() && !order.getUserId().equals(currentUser.getId())) {
      throw new ResponseStatusException(FORBIDDEN, "Access denied");
    }

    return order;
  }

  private List<OrderLine> toOrderLines(StoredCartDto cart) {
    List<OrderLine> lines = new ArrayList<>();
    if (cart == null || cart.lines() == null) {
      return lines;
    }

    for (StoredCartLineDto lineDto : cart.lines()) {
      if (lineDto.product() == null || lineDto.product().id() == null) {
        continue;
      }

      Product product = productRepository.findById(lineDto.product().id())
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));
      OrderLine line = new OrderLine();
      line.setProduct(product);
      line.setQuantity(lineDto.quantity() == null ? 0 : Math.max(lineDto.quantity(), 0));
      lines.add(line);
    }

    return lines;
  }

  private OrderTotals computeTotals(List<OrderLine> lines) {
    int itemCount = 0;
    BigDecimal total = BigDecimal.ZERO;

    for (OrderLine line : lines) {
      int quantity = line.getQuantity() == null ? 0 : Math.max(line.getQuantity(), 0);
      itemCount += quantity;
      total = total.add(line.getProduct().getPrice().multiply(BigDecimal.valueOf(quantity)));
    }

    return new OrderTotals(itemCount, total);
  }

  private String generatePaymentReference() {
    return "PAY-" + Instant.now().toEpochMilli();
  }

  private String generateInvoiceNumber(Long userId) {
    return "INV-" + userId + "-" + Instant.now().toEpochMilli();
  }

  private String defaultValue(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }

  private record OrderTotals(int itemCount, BigDecimal total) {
  }
}
