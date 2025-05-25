package org.example.pensionat_backend.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("Test")
class CustomerControllerTest {


 @Value(value ="${local.server.port}")
 private int port;

 @Autowired
 private MockMvc mockMvc;

@Test
 void testShowCustomerForm() throws Exception{
 mockMvc.perform(get("/html/CustomerReg"))
 .andExpect(status().isOk())
 .andExpect(view().name("CustomerReg"))
 .andExpect(model().attributeExists("customer"));

}
@Test
void testSubmitValidCustomerForm() throws Exception {
    mockMvc.perform(post("/html/Welcome")
                    .param("name", "Test")
                    .param("email", "testar123@example.com")
                    .param("phone", "123456789")
                    .param("ssn", "990316-1234"))
            .andExpect(status().isOk())
            .andExpect(view().name("Welcome"))
            .andExpect(model().attribute("name", "Test"));
}
@Test
 void testSubmitInvalidCustomerForm() throws Exception {
  mockMvc.perform(post("/html/Welcome")
                  .param("name", "") // tomt namn = valideringsfel
                  .param("email", "fel"))
          .andExpect(status().isOk())
          .andExpect(view().name("CustomerReg"));
 }

 @Test
 void testCustomerList() throws Exception {
  mockMvc.perform(get("/html/Customerlist"))
          .andExpect(status().isOk())
          .andExpect(view().name("CustomerList"))
          .andExpect(model().attributeExists("customers"));
 }
}





