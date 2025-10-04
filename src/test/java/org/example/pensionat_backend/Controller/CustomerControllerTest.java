package org.example.pensionat_backend.Controller;

import org.example.pensionat_backend.DTO.CustomerDTO;
import org.example.pensionat_backend.Service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mockito;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CustomerControllerTest {


 @Value(value ="${local.server.port}")
 private int port;

 @Autowired
 private MockMvc mockMvc;

 @MockitoBean
 private CustomerService customerService;

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
                  .param("name", "Testar " + System.currentTimeMillis()) // unikt namn
                  .param("email", "testar" + System.currentTimeMillis() + "@example.com") // unik e-post
                  .param("phone", "070" + (int)(Math.random() * 1000000)) // unik telefon
                  .param("ssn", "850312-" + (int)(Math.random() * 9000 + 1000))) // enkel unik ssn
          .andExpect(status().isOk())
          .andExpect(view().name("Welcome"))
          .andExpect(model().attributeExists("name"));
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


 private CustomerDTO sampleDto(Long id) {
  CustomerDTO dto = new CustomerDTO();
  dto.setId(id);
  dto.setName("Anna Andersson");
  dto.setEmail("anna@example.com");
  dto.setPhone("0701234567");
  dto.setSsn("850312-1234");
  return dto;
 }

 @Test
 void showRegisterForm() throws Exception {
  mockMvc.perform(get("/html/register"))
          .andExpect(status().isOk())
          .andExpect(view().name("CustomerReg"))
          .andExpect(model().attributeExists("customer"));
 }

 @Test
 void startsida() throws Exception {
  mockMvc.perform(get("/Startsida"))
          .andExpect(status().isOk())
          .andExpect(view().name("startsida"));
 }

 @Test
 void testDeleteCustomer_WithActiveBooking() throws Exception {
  Long customerId = 1L;
  given(customerService.customerHasBooking(customerId)).willReturn(true);
  mockMvc.perform(post("/html/deleteCustomer/{id}", customerId))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/html/Customerlist?error=Kunden har aktiva bokningar och kan inte tas bort."));

  verify(customerService, Mockito.never()).deleteById(customerId);
 }

 @Test
 void testDeleteCustomer_NoActiveBooking() throws Exception {
  Long customerId = 2L;
  given(customerService.customerHasBooking(customerId)).willReturn(false);

  mockMvc.perform(post("/html/deleteCustomer/{id}", customerId))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/html/Customerlist"));

  verify(customerService).deleteById(customerId);
 }

 @Test
 void testChangeInformation_CustomerFound() throws Exception {
  Long customerId = 1L;
  CustomerDTO mockCustomer = new CustomerDTO();
  mockCustomer.setId(customerId);
  mockCustomer.setName("Testperson");

  given(customerService.findById(customerId)).willReturn(Optional.of(mockCustomer));

  mockMvc.perform(post("/html/customer/edit/{id}", customerId))
          .andExpect(status().isOk())
          .andExpect(view().name("editCustomer"))
          .andExpect(model().attributeExists("customer"));
 }

}





