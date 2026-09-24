package com.distributedcommerce.order.repository;

import com.distributedcommerce.order.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOrderRepository implements OrderRepository {
  private Map<UUID, Order> orders = new ConcurrentHashMap<>();

  @Override
  public Order save(Order order) {
    orders.put(order.getOrderId(), order);
    return order;
  }

  @Override
  public Optional<Order> findById(UUID orderId) {
    return Optional.ofNullable(orders.get(orderId));
  }
}
