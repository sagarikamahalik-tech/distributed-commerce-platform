package com.distributedcommerce.order.controller;

import com.distributedcommerce.order.domain.Order;
import com.distributedcommerce.order.domain.OrderItem;
import com.distributedcommerce.order.dto.CreateOrderRequest;
import com.distributedcommerce.order.exception.OrderNotFoundException;
import com.distributedcommerce.order.exception.ProductNotFoundException;
import com.distributedcommerce.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private OrderService orderService;

  @Test
  void shouldCreateOrder() throws Exception {
    UUID customerId = UUID.fromString(
            "550e8400-e29b-41d4-a716-446655440000"
    );

    Order order = new Order(
            customerId,
            List.of(
                    new OrderItem(
                            "PROD-101",
                            2,
                            new BigDecimal("25.00")
                    )
            )
    );

    when(orderService.createOrder(any(CreateOrderRequest.class)))
            .thenReturn(order);

    String requestJson = """
                {
                  "customerId": "550e8400-e29b-41d4-a716-446655440000",
                  "items": [
                    {
                      "productId": "PROD-101",
                      "quantity": 2
                    }
                  ]
                }
                """;

    mockMvc.perform(
                    post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
            )
            .andExpect(status().isCreated())
            .andExpect(header().string(
                    "Location",
                    "/api/v1/orders/" + order.getOrderId()))
            .andExpect(jsonPath("$.customerId")
                    .value(customerId.toString()))
            .andExpect(jsonPath("$.totalAmount")
                    .value(50.00))
            .andExpect(jsonPath("$.status")
                    .value("CREATED"))
            .andExpect(jsonPath("$.orderId").exists());
  }


  @Test
  void shouldRejectZeroQuantity() throws Exception {
    String invalidJson = """
            {
              "customerId": "550e8400-e29b-41d4-a716-446655440000",
              "items": [
                {
                  "productId": "PROD-101",
                  "quantity": 0
                }
              ]
            }
            """;

    mockMvc.perform(
                    post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson)
            )
            .andExpect(status().isBadRequest());

    verifyNoInteractions(orderService);
  }


  @Test
  void shouldReturnBadRequestForUnknownProduct() throws Exception {
    // explain step by step what this code does
    // 1. The test method `shouldReturnBadRequestForUnknownProduct` is defined to check the behavior of the `OrderController` when an order is created with an unknown product ID.
    // 2. A mock behavior is set up for the `orderService.createOrder` method using Mockito's `when` and `thenThrow`. It specifies that when the `createOrder` method is called with any `CreateOrderRequest`, it should throw an `IllegalArgumentException` with the message "Unknown product: PROD-999".
    when(orderService.createOrder(any(CreateOrderRequest.class)))
            .thenThrow(new ProductNotFoundException("PROD-999"));

    String requestJson = """
            {
              "customerId": "550e8400-e29b-41d4-a716-446655440000",
              "items": [
                {
                  "productId": "PROD-999",
                  "quantity": 1
                }
              ]
            }
            """;

    mockMvc.perform(
                    post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                    .value("Unknown product: PROD-999")).andDo(print());
  }


  @Test
  void shouldRetrieveExistingOrder() throws Exception {
    UUID customerId = UUID.randomUUID();

    Order existingOrder = new Order(
            customerId,
            List.of(
                    new OrderItem(
                            "PROD-101",
                            2,
                            new BigDecimal("25.00")
                    )
            )
    );

    UUID orderId = existingOrder.getOrderId();

    when(orderService.getOrderById(orderId))
            .thenReturn(existingOrder);

    mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId")
                    .value(orderId.toString()))
            .andExpect(jsonPath("$.customerId")
                    .value(customerId.toString()))
            .andExpect(jsonPath("$.totalAmount")
                    .value(50.00))
            .andExpect(jsonPath("$.status")
                    .value("CREATED"));

    verify(orderService).getOrderById(orderId);
  }


  @Test
  void shouldReturnNotFoundForUnknownOrder() throws Exception {
    UUID unknownOrderId = UUID.randomUUID();

    when(orderService.getOrderById(unknownOrderId))
            .thenThrow(
                    new OrderNotFoundException(unknownOrderId)
            );

    mockMvc.perform(
                    get(
                            "/api/v1/orders/{orderId}",
                            unknownOrderId
                    )
            )
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message")
                    .value(
                            "Order not found: " + unknownOrderId
                    ));

    verify(orderService).getOrderById(unknownOrderId);
  }
}
