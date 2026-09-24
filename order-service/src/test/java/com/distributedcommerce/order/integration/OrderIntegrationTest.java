package com.distributedcommerce.order.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldCreateAndRetrieveOrder() throws Exception {

    String requestJson = """
            {
              "customerId": "550e8400-e29b-41d4-a716-446655440000",
              "items": [
                {
                  "productId": "PROD-101",
                  "quantity": 2
                },
                {
                  "productId": "PROD-205",
                  "quantity": 1
                }
              ]
            }
            """;

    MvcResult postResult = mockMvc.perform(
                    post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.totalAmount").value(130.00))
            .andExpect(jsonPath("$.status").value("CREATED"))
            .andReturn();

    String location = postResult
            .getResponse()
            .getHeader("Location");

    assertNotNull(location);

    mockMvc.perform(get(location))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId")
                    .value("550e8400-e29b-41d4-a716-446655440000"))
            .andExpect(jsonPath("$.totalAmount").value(130.00))
            .andExpect(jsonPath("$.status").value("CREATED"))
            .andExpect(jsonPath("$.orderId")
                    .value(location.substring(location.lastIndexOf('/') + 1)));
  }
}
