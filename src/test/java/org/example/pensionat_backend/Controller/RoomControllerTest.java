package org.example.pensionat_backend.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testShowAllRooms() throws Exception {
        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms"))
                .andExpect(model().attributeExists("rooms"));
    }

    @Test
    void testShowSearchForm() throws Exception {
        mockMvc.perform(get("/rooms/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("searchRooms"));
    }

    @Test
    void testShowAvailableRooms() throws Exception {
        mockMvc.perform(get("/rooms/search/results")
                        .param("guests", "2")
                        .param("checkIn", "2030-06-10")
                        .param("checkOut", "2030-06-12"))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms"))
                .andExpect(model().attributeExists("rooms"));
    }
}
