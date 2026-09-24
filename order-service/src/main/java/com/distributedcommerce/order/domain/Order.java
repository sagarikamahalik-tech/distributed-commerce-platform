package com.distributedcommerce.order.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Order {
  private final UUID orderId;
  private final UUID customerId;
  private final List<OrderItem> items;
  private final BigDecimal totalAmount;
  private final Instant createdAt;
  // why status is not final: The status field is not declared as final because the order's status can change over time. For example, an order may start with a status of CREATED and later transition to other statuses such as INVENTORY_RESERVED, PAYMENT_PENDING, CONFIRMED, CANCELLED, or FAILED. Making the status field mutable allows the system to update the order's status as it progresses through different stages of processing.
  // why intellij suggesting to make status final: IntelliJ IDEA may suggest making the status field final if it detects that the field is only assigned once (in the constructor) and never modified afterward. However, in this case, the status is expected to change over time, so it should not be made final. The suggestion can be ignored because the design of the Order class requires the status to be mutable to reflect the order's lifecycle.
  private OrderStatus status;

  public Order(UUID customerId, List<OrderItem> items) {
    // TODO 1: Validate customerId.
    // explain: We check if the customerId is null and throw an IllegalArgumentException if it is.
    if (customerId == null) {
      throw new IllegalArgumentException("Customer ID is required");
    }
    // TODO 2: Validate items.
    // explain: We check if the items list is null or empty and throw an IllegalArgumentException if it is.
    if (items == null || items.isEmpty()) {
      throw new IllegalArgumentException("Order must contain at least one item");
    }
    // TODO 3: Generate a UUID for orderId.
    // explain: We generate a unique identifier for the order using UUID.randomUUID().
    this.orderId = UUID.randomUUID();
    this.customerId = customerId;
    // TODO 4: Make a defensive copy of items.
    // explain: We use List.copyOf to create an unmodifiable copy of the items list.
    this.items = List.copyOf(items);
    // TODO 5: Calculate totalAmount.
    this.totalAmount = calculateTotal();
    // TODO 6: Set status to CREATED.
    // explain: We set the initial status of the order to CREATED.
    this.status = OrderStatus.CREATED;
    // TODO 7: Set createdAt.
    // explain: We set the creation timestamp to the current time.
    this.createdAt = Instant.now();
  }

  // TODO 8: Implement calculateTotal().
  // explain: This method calculates the total amount of the order
  // by summing up the subtotals of each OrderItem in the items list.
  // It uses Java Streams to map each OrderItem to its subtotal and
  // then reduces the stream to a single BigDecimal value representing the total amount.
  private BigDecimal calculateTotal() {
    return items.stream()
            .map(OrderItem::subtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  // TODO 9: Implement reserveInventory().
  // explain: This method is responsible for reserving inventory for the order.
  // It checks if the current status of the order is CREATED. If not,
  // it throws an IllegalStateException, indicating that inventory can only be
  // reserved when the order is in the CREATED state
  public void reserveInventory() {
    if (status != OrderStatus.CREATED) {
      throw new IllegalStateException(
              "Inventory can only be reserved " +
                      "when the order is CREATED"
      );
    }
    this.status = OrderStatus.INVENTORY_RESERVED;
  }

  public void startPayment() {
    if (status != OrderStatus.INVENTORY_RESERVED) {
      throw new IllegalStateException(
              "Payment can only start after inventory is reserved"
      );
    }

    this.status = OrderStatus.PAYMENT_PENDING;
  }

  public void confirmPayment() {
    if (status != OrderStatus.PAYMENT_PENDING) {
      throw new IllegalStateException(
              "Payment can only be confirmed when payment is pending"
      );
    }

    this.status = OrderStatus.CONFIRMED;
  }

  public void failPayment() {
    if (status != OrderStatus.PAYMENT_PENDING) {
      throw new IllegalStateException(
              "Payment can only fail when payment is pending"
      );
    }

    this.status = OrderStatus.FAILED;
  }

  public void cancel() {
    if (status == OrderStatus.CANCELLED ||
            status == OrderStatus.FAILED) {
      throw new IllegalStateException(
              "Order cannot be cancelled from status: " + status
      );
    }

    this.status = OrderStatus.CANCELLED;
  }

  // TODO 11: Add getters.
  // explain: These getter methods provide access to the private fields of the Order class.
  public UUID getOrderId() {
    return orderId;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public List<OrderItem> getItems() {
    return items;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public OrderStatus getStatus() {
    return status;
  }
}
