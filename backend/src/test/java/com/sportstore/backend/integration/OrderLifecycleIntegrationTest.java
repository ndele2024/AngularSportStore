package com.sportstore.backend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderLifecycleIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void authenticatedUserCanCreateOrderAndDownloadInvoice() throws Exception {
    String userToken = login("jane", "password");

    MvcResult creation = mockMvc.perform(post("/orders")
        .header("Authorization", "Bearer<" + userToken + ">")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
            "nom": "Doe",
            "prenom": "Jane",
            "adresse": "25 Market Street",
            "telephone": "555-111-2233",
            "paymentStatus": "PAYE",
            "paymentMethod": "CARTE_CREDIT",
            "paymentReference": "PAY-TEST-100",
            "paymentLast4": "4242",
            "cart": {
              "lines": [
                {
                  "product": { "id": 1 },
                  "quantity": 2
                }
              ]
            }
          }
          """))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.status").value("EN_TRAITEMENT"))
      .andExpect(jsonPath("$.paymentStatus").value("PAYE"))
      .andExpect(jsonPath("$.invoiceNumber").isNotEmpty())
      .andReturn();

    long orderId = objectMapper.readTree(creation.getResponse().getContentAsString()).get("id").asLong();

    mockMvc.perform(get("/orders/" + orderId + "/invoice")
        .header("Authorization", "Bearer<" + userToken + ">"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_PDF));
  }

  @Test
  void adminCanMarkOrderAsDelivered() throws Exception {
    String userToken = login("jane", "password");
    String adminToken = login("admin", "secret");

    MvcResult creation = mockMvc.perform(post("/orders")
        .header("Authorization", "Bearer<" + userToken + ">")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
            "nom": "Doe",
            "prenom": "Jane",
            "adresse": "25 Market Street",
            "telephone": "555-111-2233",
            "paymentStatus": "PAYE",
            "paymentMethod": "CARTE_CREDIT",
            "paymentReference": "PAY-TEST-200",
            "paymentLast4": "1111",
            "cart": {
              "lines": [
                {
                  "product": { "id": 2 },
                  "quantity": 1
                }
              ]
            }
          }
          """))
      .andExpect(status().isCreated())
      .andReturn();

    long orderId = objectMapper.readTree(creation.getResponse().getContentAsString()).get("id").asLong();

    mockMvc.perform(put("/orders/" + orderId)
        .header("Authorization", "Bearer<" + adminToken + ">")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
            "status": "LIVRE",
            "shipped": true
          }
          """))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("LIVRE"))
      .andExpect(jsonPath("$.shipped").value(true))
      .andExpect(jsonPath("$.deliveredAt").isNotEmpty());
  }

  private String login(String username, String password) throws Exception {
    MvcResult result = mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
            "username": "%s",
            "password": "%s"
          }
          """.formatted(username, password)))
      .andExpect(status().isOk())
      .andReturn();

    JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
    return json.get("token").asText();
  }
}
