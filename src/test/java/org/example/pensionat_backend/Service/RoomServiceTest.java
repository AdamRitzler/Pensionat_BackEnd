package org.example.pensionat_backend.Service;

import org.example.pensionat_backend.Models.Booking;
import org.example.pensionat_backend.Models.Customer;
import org.example.pensionat_backend.Models.Room;
import org.example.pensionat_backend.Models.RoomType;
import org.example.pensionat_backend.Repository.CustomerRepository;
import org.example.pensionat_backend.Repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class RoomServiceTest {

    @Autowired
    private CustomerRepository customerRepository;


    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    private Room room;

    @BeforeEach
    void setUp() {
        roomRepository.deleteAll();

        room = new Room();
        room.setRoomNumber("R101");
        room.setRoomType(RoomType.DOUBLE);
        room.setMaxExtraBeds(1);
        room.setAvailable(true);
        roomRepository.save(room);
    }

    @Test
    void shouldReturnAllRooms() {
        List<Room> allRooms = roomService.findAllRooms();
        assertFalse(allRooms.isEmpty());
        assertEquals("R101", allRooms.get(0).getRoomNumber());
    }

    @Test
    void shouldReturnRoomWhenEnoughCapacity() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        List<Room> available = roomService.findAvailableRoomsFor(3, checkIn, checkOut); // DOUBLE + 1 = 3
        assertFalse(available.isEmpty());
        assertEquals("R101", available.get(0).getRoomNumber());
    }

    @Test
    void shouldNotReturnRoomWhenOverCapacity() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        List<Room> available = roomService.findAvailableRoomsFor(4, checkIn, checkOut); // Max 3 personer
        assertTrue(available.isEmpty());
    }

    @Test
    void shouldNotReturnRoomWhenBooked() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setStartDate(checkIn);
        booking.setEndDate(checkOut);
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer.setPhone("1234567");
        customer.setSsn("9001011234");
        customerRepository.save(customer);
        booking.setCustomer(customer);

        room.getBookings().add(booking);
        roomRepository.save(room);

        List<Room> available = roomService.findAvailableRoomsFor(2, checkIn, checkOut);
        assertTrue(available.isEmpty());
    }
    @Test
    void shouldFindRoomById() {
        Optional<Room> foundRoom = roomService.findById(room.getId());

        assertTrue(foundRoom.isPresent());
        assertEquals(room.getRoomNumber(), foundRoom.get().getRoomNumber());
    }
}
