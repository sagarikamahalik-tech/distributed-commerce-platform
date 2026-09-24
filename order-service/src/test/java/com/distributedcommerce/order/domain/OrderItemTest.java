package com.distributedcommerce.order.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderItemTest {

  @Test
  void shouldCalculateSubtotal() {
    OrderItem item = new OrderItem(
            "PROD-101",
            2,
            new BigDecimal("25.00")
    );

    assertEquals(
            new BigDecimal("50.00"),
            item.subtotal()
    );
  }

  @Test
  void shouldRejectNegativeQuantity() {
    assertThrows(
            IllegalArgumentException.class,
            () -> new OrderItem(
                    "PROD-101",
                    -2,
                    new BigDecimal("25.00")
            )
    );
  }
}
