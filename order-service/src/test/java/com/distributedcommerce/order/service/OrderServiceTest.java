package com.distributedcommerce.order.service;

import com.distributedcommerce.order.domain.Order;
import com.distributedcommerce.order.domain.OrderItem;
import com.distributedcommerce.order.domain.OrderStatus;
import com.distributedcommerce.order.dto.CreateOrderItemRequest;
import com.distributedcommerce.order.dto.CreateOrderRequest;
import com.distributedcommerce.order.exception.OrderNotFoundException;
import com.distributedcommerce.order.exception.ProductNotFoundException;
import com.distributedcommerce.order.repository.InMemoryOrderRepository;
import com.distributedcommerce.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

  private final OrderRepository orderRepository =
          mock(OrderRepository.class);

  private final OrderService orderService =
          new OrderService(orderRepository);

  @Test
  void shouldCreateOrderUsingServerSidePrices() {
    UUID customerId = UUID.randomUUID();

    CreateOrderRequest request =
            new CreateOrderRequest(
                    customerId,
                    List.of(
                            new CreateOrderItemRequest(
                                    "PROD-101", 2
                            ),
                            new CreateOrderItemRequest(
                                    "PROD-205", 1
                            )
                    )
            );

    // Mock the repository to return the order with a generated ID

    when(orderRepository.save(any(Order.class)))
            .thenAnswer(invocation -> {
              Order savedOrder = invocation.getArgument(0);
              return savedOrder;
            });

    Order order = orderService.createOrder(request);

    assertNotNull(order.getOrderId());
    assertEquals(customerId, order.getCustomerId());
    assertEquals(
            new BigDecimal("130.00"),
            order.getTotalAmount()
    );
    assertEquals(
            OrderStatus.CREATED,
            order.getStatus()
    );

    verify(orderRepository).save(any(Order.class));
  }


  @Test
  void shouldRejectUnknownProduct() {
    CreateOrderRequest request = new CreateOrderRequest(
            UUID.randomUUID(),
            List.of(
                    new CreateOrderItemRequest("PROD-999", 1)
            )
    );

    ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class,
            () -> orderService.createOrder(request)
    );

    assertEquals(
            "Unknown product: PROD-999",
            exception.getMessage()
    );
    verifyNoInteractions(orderRepository);
  }


  @Test
  void shouldRetrieveExistingOrder() {
    Order existingOrder = new Order(
            UUID.randomUUID(),
            List.of(
                    new OrderItem(
                            "PROD-101",
                            2,
                            new BigDecimal("25.00")
                    )
            )
    );

    UUID orderId = existingOrder.getOrderId();

    when(orderRepository.findById(orderId))
            .thenReturn(Optional.of(existingOrder));

    Order result = orderService.getOrderById(orderId);

    assertSame(existingOrder, result);
    verify(orderRepository).findById(orderId);
  }


  @Test
  void shouldThrowWhenOrderDoesNotExist() {
    UUID unknownOrderId = UUID.randomUUID();

    when(orderRepository.findById(unknownOrderId))
            .thenReturn(Optional.empty());

    OrderNotFoundException exception = assertThrows(
            OrderNotFoundException.class,
            () -> orderService.getOrderById(unknownOrderId)
    );

    assertEquals(
            "Order not found: " + unknownOrderId,
            exception.getMessage()
    );

    verify(orderRepository).findById(unknownOrderId);
  }
}
