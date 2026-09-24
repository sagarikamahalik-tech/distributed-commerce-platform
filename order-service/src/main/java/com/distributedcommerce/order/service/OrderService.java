package com.distributedcommerce.order.service;

import com.distributedcommerce.order.domain.Order;
import com.distributedcommerce.order.domain.OrderItem;
import com.distributedcommerce.order.dto.CreateOrderRequest;
import com.distributedcommerce.order.exception.OrderNotFoundException;
import com.distributedcommerce.order.exception.ProductNotFoundException;
import com.distributedcommerce.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {
  private final OrderRepository orderRepository;

  private final Map<String, BigDecimal> productPrices =
          Map.of(
                  "PROD-101", new BigDecimal("25.00"),
                  "PROD-205", new BigDecimal("80.00")
          );

  public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public Order createOrder(CreateOrderRequest request) {

    List<OrderItem> items = request.items()
            .stream()
            .map(item -> {
              BigDecimal price =
                      productPrices.get(item.productId());

              if (price == null) {
                throw new ProductNotFoundException(item.productId());
              }

              return new OrderItem(
                      item.productId(),
                      item.quantity(),
                      price
              );
            })
            .toList();

    Order order = new Order(request.customerId(), items);
    return orderRepository.save(order);
  }

  public Order getOrderById(UUID orderId) {
    return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
  }
}
