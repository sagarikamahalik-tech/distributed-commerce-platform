package com.distributedcommerce.order.repository;

import com.distributedcommerce.order.domain.Order;
import com.distributedcommerce.order.domain.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryOrderRepositoryTest {
  private final OrderRepository repository =
          new InMemoryOrderRepository();

  @Test
  void shouldSaveAndRetrieveOrder() {
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

    repository.save(order);

    Optional<Order> result =
            repository.findById(order.getOrderId());

    assertTrue(result.isPresent());
    assertEquals(
            order.getOrderId(),
            result.get().getOrderId()
    );
    assertEquals(
            new BigDecimal("50.00"),
            result.get().getTotalAmount()
    );
  }

  @Test
  void shouldReturnEmptyForUnknownOrder() {
    UUID unknownOrderId = UUID.randomUUID();

    Optional<Order> result =
            repository.findById(unknownOrderId);

    assertTrue(result.isEmpty());
  }
}
