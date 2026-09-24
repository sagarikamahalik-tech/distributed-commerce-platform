package com.distributedcommerce.order.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateOrderRequestValidationTest {
  // explain why we are using a validator here
  // We are using a Validator here to programmatically validate the
  // CreateOrderRequest object against the constraints defined in its class.
  // This allows us to test that the validation annotations
  // (like @NotNull, @NotEmpty, and @Valid) are working as expected,
  // ensuring that invalid data is correctly rejected before processing.
  private final Validator validator =
          Validation.buildDefaultValidatorFactory().getValidator();
  @Test
  void shouldRejectZeroQuantity() {
    CreateOrderRequest request =
            new CreateOrderRequest(
                    UUID.randomUUID(),
                    List.of(
                            new CreateOrderItemRequest(
                                    "PROD-101",
                                    0
                            )
                    )
            );

    var violations = validator.validate(request);

    assertTrue(
            violations.stream().anyMatch(
                    violation -> violation
                            .getPropertyPath()
                            .toString()
                            .contains("quantity")
            )
    );
  }
}
