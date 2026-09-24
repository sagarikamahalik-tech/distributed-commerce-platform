package com.distributedcommerce.order.dto;

import com.distributedcommerce.order.domain.Order;
import com.distributedcommerce.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(UUID orderId,
                            UUID customerId,
                            BigDecimal totalAmount,
                            OrderStatus status,
                            Instant createdAt) {
  public static OrderResponse from(Order order) {
    return new OrderResponse(
            order.getOrderId(),
            order.getCustomerId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getCreatedAt()
    );
  }
}
