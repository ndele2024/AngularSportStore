package com.sportstore.backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthAndProductIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void loginReturnsTokenForSeededAdmin() throws Exception {
    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
            "username": "admin",
            "password": "secret"
          }
          """))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.token").isNotEmpty())
      .andExpect(jsonPath("$.user.username").value("admin"));
  }

  @Test
  void productsAreAccessibleWithoutAuthentication() throws Exception {
    mockMvc.perform(get("/products"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].name").exists());
  }

  @Test
  void creatingProductRequiresAdminAuthentication() throws Exception {
    mockMvc.perform(post("/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
            "name": "Test Product",
            "category": "Test",
            "description": "Test description",
            "price": 10.00,
            "imageUrl": "https://example.com/test.jpg"
          }
          """))
      .andExpect(status().isForbidden());
  }
}
