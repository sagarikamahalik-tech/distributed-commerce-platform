package com.distributedcommerce.order.domain;

public enum OrderStatus {
  CREATED,
  INVENTORY_RESERVED,
  PAYMENT_PENDING,
  CONFIRMED,
  CANCELLED,
  FAILED
}
