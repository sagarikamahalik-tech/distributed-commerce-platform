package com.distributedcommerce.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleProductNotFound(ProductNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", exception.getMessage()));
  }

  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleOrderNotFound(
          OrderNotFoundException exception
  ) {
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(Map.of(
                    "message",
                    exception.getMessage()
            ));
  }
}
