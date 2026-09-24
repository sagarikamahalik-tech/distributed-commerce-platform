package com.distributedcommerce.order.domain;

import java.math.BigDecimal;

public record OrderItem(String productId,
                        int quantity,
                        BigDecimal unitPrice) {
  public OrderItem {
    if (productId == null || productId.isBlank()) {
      throw new IllegalArgumentException(
              "Product ID is required");
    }

    if (quantity <= 0) {
      throw new IllegalArgumentException(
              "Quantity must be positive");
    }

    if (unitPrice == null ||
            unitPrice.signum() < 0) {
      throw new IllegalArgumentException(
              "Unit price cannot be negative");
    }
  }

  public BigDecimal subtotal() {
    return unitPrice.multiply(
            BigDecimal.valueOf(quantity));
  }
}
