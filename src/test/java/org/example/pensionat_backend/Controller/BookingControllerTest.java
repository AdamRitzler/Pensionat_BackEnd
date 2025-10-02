package org.example.pensionat_backend.Controller;

import org.example.pensionat_backend.Models.Booking;
import org.example.pensionat_backend.Models.Customer;
import org.example.pensionat_backend.Models.Room;
import org.example.pensionat_backend.Models.RoomType;
import org.example.pensionat_backend.Service.BookingService;
import org.example.pensionat_backend.Service.CustomerService;
import org.example.pensionat_backend.Service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @Mock
    private CustomerService customerService;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private BookingController bookingController;

    private Customer testCustomer;
    private Room testRoom;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");

        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setRoomNumber("101");
        testRoom.setRoomType(RoomType.SINGLE);

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setCustomer(testCustomer);
        testBooking.setRoom(testRoom);
        testBooking.setStartDate(LocalDate.of(2025, 6, 1));
        testBooking.setEndDate(LocalDate.of(2025, 6, 5));
    }

    @Test
    void testShowBookingForm() throws Exception {
        when(roomService.findById(1L)).thenReturn(Optional.of(testRoom));

        mockMvc.perform(get("/book/start").param("roomId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookingForm"))
                .andExpect(model().attributeExists("room"));
    }

    @Test
    void testCreateBookingSuccess() throws Exception {
        when(bookingService.createBooking(any(), any(), any(), any())).thenReturn(testBooking);

        mockMvc.perform(post("/book/create")
                        .param("customerId", "1")
                        .param("roomId", "1")
                        .param("startDate", "2030-06-01")
                        .param("endDate", "2030-06-05"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookingConfirmation"));
    }

    @Test
    void testCreateBookingFailure() throws Exception {
        when(bookingService.createBooking(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Kunde inte skapa bokning"));

        mockMvc.perform(post("/book/create")
                        .param("customerId", "1")
                        .param("roomId", "1")
                        .param("startDate", "2030-06-01")
                        .param("endDate", "2030-06-05"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/start?roomId=1"));
    }

    @Test
    void testShowCustomersWithBookings() throws Exception {
        testCustomer.setBookings(List.of(testBooking));
        when(customerService.findCustomersWithBookings()).thenReturn(List.of(testCustomer));

        mockMvc.perform(get("/book/cancel"))
                .andExpect(status().isOk())
                .andExpect(view().name("cancelBooking"))
                .andExpect(model().attributeExists("customers"));
    }

    @Test
    void testCancelBooking() throws Exception {
        Mockito.doNothing().when(bookingService).deleteBooking(1L);

        mockMvc.perform(post("/book/cancel")
                        .param("bookingId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/cancel"));
    }
}
