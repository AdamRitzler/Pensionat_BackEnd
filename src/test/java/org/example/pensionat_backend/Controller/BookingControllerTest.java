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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("Test")
public class BookingControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Value("${local.server.port}")
    private int port;


    @Test
    void testShowBookingForm() throws Exception {
        mockMvc.perform(get("/book/start")
                        .param("roomId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookingForm"))
                .andExpect(model().attributeExists("room"));
    }

    @Test
    void testCreateBookingSuccess() throws Exception {
        mockMvc.perform(post("/book/create")
                        .param("customerId", "3")
                        .param("roomId", "201")
                        .param("startDate", "2025-06-01")
                        .param("endDate", "2025-06-05"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookingConfirmation"))
                .andExpect(model().attributeExists("booking"))
                .andExpect(model().attributeExists("room"))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void testCreateBookingFailure() throws Exception {

        mockMvc.perform(post("/book/create")
                        .param("customerId", "999") // ogiltigt ID
                        .param("roomId", "999")
                        .param("startDate", "2025-06-01")
                        .param("endDate", "2025-06-05"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookingConfirmation"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void testShowCustomersWithBookings() throws Exception {
        mockMvc.perform(get("/book/cancel"))
                .andExpect(status().isOk())
                .andExpect(view().name("cancelBooking"))
                .andExpect(model().attributeExists("customers"));
    }

    @Test
    void testCancelBooking() throws Exception {
        mockMvc.perform(post("/book/cancel")
                        .param("bookingId", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/cancel"));
    }
}
