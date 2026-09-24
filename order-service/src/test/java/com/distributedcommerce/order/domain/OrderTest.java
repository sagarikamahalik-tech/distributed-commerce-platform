package com.distributedcommerce.order.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderTest {

  @Test
  void shouldCreateOrderWithValidItems() {
    OrderItem item1 = new OrderItem(
            "PROD-101",
            2,
            new BigDecimal("25.00")
    );

    OrderItem item2 = new OrderItem(
            "PROD-205",
            1,
            new BigDecimal("80.00")
    );

    Order order = new Order(
            UUID.randomUUID(),
            List.of(item1, item2)
    );

    assertEquals(2, order.getItems().size());
  }

  @Test
  void shouldCalculateTotalAmount() {
    OrderItem item1 = new OrderItem(
            "PROD-101",
            2,
            new BigDecimal("25.00")
    );

    OrderItem item2 = new OrderItem(
            "PROD-205",
            1,
            new BigDecimal("80.00")
    );

    Order order = new Order(
            UUID.randomUUID(),
            List.of(item1, item2)
    );

    assertEquals(
            new BigDecimal("130.00"),
            order.getTotalAmount()
    );
  }

  @Test
  void shouldRejectNullCustomerId() {
    assertThrows(
            IllegalArgumentException.class,
            () -> new Order(
                    null,
                    List.of(new OrderItem("PROD-101", 2, new BigDecimal("25.00")))
            )
    );
  }

  @Test
  void shouldRejectEmptyItems() {
    assertThrows(
            IllegalArgumentException.class,
            () -> new Order(
                    UUID.randomUUID(),
                    List.of()
            )
    );
  }

  @Test
  void shouldRejectNullItems() {
    assertThrows(
            IllegalArgumentException.class,
            () -> new Order(
                    UUID.randomUUID(),
                    null
            )
    );
  }

  @Test
  void shouldInitializeStatusAsCreated() {
    Order order = new Order(
            UUID.randomUUID(),
            List.of(new OrderItem("PROD-101", 2, new BigDecimal("25.00")))
    );

    assertEquals(OrderStatus.CREATED, order.getStatus());
  }

  @Test
  void shouldSetCreationTimestamp() {
    Order order = new Order(
            UUID.randomUUID(),
            List.of(new OrderItem("PROD-101", 2, new BigDecimal("25.00")))
    );

    assertEquals(true, order.getCreatedAt() != null);
  }

  @Test
  void shouldPreventExternalModificationOfItems() {
    OrderItem item = new OrderItem(
            "PROD-101",
            2,
            new BigDecimal("25.00")
    );

    Order order = new Order(
            UUID.randomUUID(),
            List.of(item)
    );

    assertThrows(
            UnsupportedOperationException.class,
            () -> order.getItems().add(new OrderItem("PROD-205", 1, new BigDecimal("80.00")))
    );
  }


  @Test
  void shouldReserveInventoryForCreatedOrder() {
    Order order = new Order(
            UUID.randomUUID(),
            List.of(
                    new OrderItem(
                            "PROD-101",
                            2,
                            new BigDecimal("25.00")
                    )
            )
    );

    order.reserveInventory();

    assertEquals(
            OrderStatus.INVENTORY_RESERVED,
            order.getStatus()
    );
  }

  @Test
  void shouldRejectRepeatedInventoryReservation() {
    Order order = new Order(
            UUID.randomUUID(),
            List.of(
                    new OrderItem(
                            "PROD-101",
                            2,
                            new BigDecimal("25.00")
                    )
            )
    );

    order.reserveInventory();

    assertThrows(
            IllegalStateException.class,
            order::reserveInventory
    );

    assertEquals(
            OrderStatus.INVENTORY_RESERVED,
            order.getStatus()
    );
  }


  @Test
  void shouldCompleteOrderLifecycle() {
    Order order = new Order(
            UUID.randomUUID(),
            List.of(
                    new OrderItem(
                            "PROD-101",
                            2,
                            new BigDecimal("25.00")
                    )
            )
    );

    assertEquals(OrderStatus.CREATED, order.getStatus());

    order.reserveInventory();
    assertEquals(
            OrderStatus.INVENTORY_RESERVED,
            order.getStatus()
    );

    order.startPayment();
    assertEquals(
            OrderStatus.PAYMENT_PENDING,
            order.getStatus()
    );

    order.confirmPayment();
    assertEquals(
            OrderStatus.CONFIRMED,
            order.getStatus()
    );
  }


  @Test
  void shouldAllowCancellationAfterConfirmation() {
    Order order = new Order(
            UUID.randomUUID(),
            List.of(
                    new OrderItem(
                            "PROD-101",
                            2,
                            new BigDecimal("25.00")
                    )
            )
    );

    order.reserveInventory();
    order.startPayment();
    order.confirmPayment();

    order.cancel();

    assertEquals(
            OrderStatus.CANCELLED,
            order.getStatus()
    );

    assertThrows(
            IllegalStateException.class,
            order::reserveInventory
    );
  }


  @Test
  void shouldNotConfirmOrderWhenPaymentFails() {
    Order order = new Order(
            UUID.randomUUID(),
            List.of(
                    new OrderItem(
                            "PROD-101",
                            2,
                            new BigDecimal("25.00")
                    )
            )
    );

    order.reserveInventory();
    order.startPayment();

    order.failPayment();

    assertEquals(
            OrderStatus.FAILED,
            order.getStatus()
    );

    assertThrows(
            IllegalStateException.class,
            order::confirmPayment
    );

    assertEquals(
            OrderStatus.FAILED,
            order.getStatus()
    );
  }


  @Test
  void shouldRejectConfirmationBeforePaymentStarts() {
    Order order = new Order(
            UUID.randomUUID(),
            List.of(
                    new OrderItem(
                            "PROD-101",
                            2,
                            new BigDecimal("25.00")
                    )
            )
    );

    assertThrows(
            IllegalStateException.class,
            order::confirmPayment
    );

    assertEquals(
            OrderStatus.CREATED,
            order.getStatus()
    );
  }

  @Test
  void shouldRemainPendingWhileAwaitingPaymentResult() {
    Order order = new Order(
            UUID.randomUUID(),
            List.of(
                    new OrderItem(
                            "PROD-101",
                            2,
                            new BigDecimal("25.00")
                    )
            )
    );

    order.reserveInventory();
    order.startPayment();

    // No payment result has been received yet.
    assertEquals(
            OrderStatus.PAYMENT_PENDING,
            order.getStatus()
    );
  }
}