package com.sportstore.backend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private String userToken;

  @BeforeEach
  void authenticate() throws Exception {
    MvcResult result = mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
            "username": "jane",
            "password": "password"
          }
          """))
      .andExpect(status().isOk())
      .andReturn();

    JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
    userToken = json.get("token").asText();
  }

  @Test
  void authenticatedUserCanUpdateOwnProfileAndCart() throws Exception {
    mockMvc.perform(patch("/users/2")
        .header("Authorization", "Bearer<" + userToken + ">")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
            "nom": "Doe Updated",
            "telephone": "555-999-0000",
            "cart": {
              "lines": [
                {
                  "product": { "id": 1 },
                  "quantity": 2
                }
              ],
              "itemCount": 2,
              "cartPrice": 550.00
            }
          }
          """))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.nom").value("Doe Updated"))
      .andExpect(jsonPath("$.telephone").value("555-999-0000"))
      .andExpect(jsonPath("$.cart.lines[0].product.id").value(1))
      .andExpect(jsonPath("$.cart.lines[0].quantity").value(2));
  }
}
