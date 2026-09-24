package com.distributedcommerce.order.controller;

import com.distributedcommerce.order.domain.Order;
import com.distributedcommerce.order.dto.CreateOrderRequest;
import com.distributedcommerce.order.dto.OrderResponse;
import com.distributedcommerce.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(
          @Valid @RequestBody CreateOrderRequest request
  ) {
    Order order = orderService.createOrder(request);

    URI location = URI.create(
            "/api/v1/orders/" + order.getOrderId()
    );

    return ResponseEntity
            .created(location)
            .body(OrderResponse.from(order));
  }

  @GetMapping("/{orderId}")
  public OrderResponse getOrderById(@PathVariable UUID orderId) {
    // Logic to retrieve an order by ID will go here
    Order order = orderService.getOrderById(orderId);
    return OrderResponse.from(order);
  }
}
