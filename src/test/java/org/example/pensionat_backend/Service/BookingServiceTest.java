package org.example.pensionat_backend.Service;
import jakarta.persistence.EntityManager;

import org.example.pensionat_backend.DTO.BookingDTO;
import org.example.pensionat_backend.Models.Booking;
import org.example.pensionat_backend.Models.Customer;
import org.example.pensionat_backend.Models.Room;
import org.example.pensionat_backend.Models.RoomType;
import org.example.pensionat_backend.Repository.BookingRepository;
import org.example.pensionat_backend.Repository.CustomerRepository;
import org.example.pensionat_backend.Repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
@ActiveProfiles("test")
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EntityManager entityManager;


    private Customer testCustomer;
    private Room testRoom;

    @BeforeEach
    void setup() {
        testCustomer = new Customer();
        testCustomer.setName("Testare");
        testCustomer.setEmail("test@example.com");
        testCustomer.setPhone("0700000000");
        testCustomer.setSsn("200101-1234");
        customerRepository.save(testCustomer);

        testRoom = new Room();
        testRoom.setRoomNumber("R101");
        testRoom.setRoomType(RoomType.DOUBLE);
        testRoom.setMaxExtraBeds(1);
        testRoom.setBookings(new ArrayList<>()); // ✅ Viktigt!
        roomRepository.save(testRoom);
    }

    @Test
    void createAndRetrieveBooking() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusDays(2);

        Booking created = bookingService.createBooking(testRoom.getId(), testCustomer.getId(), start, end);
        assertNotNull(created.getId());

        BookingDTO fetched = bookingService.getBookingById(created.getId());
        assertEquals(testCustomer.getId(), fetched.getCustomerId());
        assertEquals(testRoom.getId(), fetched.getRoomId());
    }

    @Test
    void shouldNotAllowBookingInThePast() {
        LocalDate start = LocalDate.now().minusDays(2);
        LocalDate end = LocalDate.now().plusDays(1);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                bookingService.createBooking(testRoom.getId(), testCustomer.getId(), start, end));

        assertTrue(ex.getMessage().contains("Startdatum kan inte vara i det förflutna"));
    }

    @Test
    void shouldNotAllowOverlappingBooking() {
        LocalDate start = LocalDate.now().plusDays(3);
        LocalDate end = start.plusDays(3);


        bookingService.createBooking(testRoom.getId(), testCustomer.getId(), start, end);


        entityManager.flush();
        entityManager.clear();


        testRoom = roomRepository.findByIdWithBookings(testRoom.getId()).orElseThrow();
        System.out.println("Antal bokningar i rummet: " + testRoom.getBookings().size());


        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                bookingService.createBooking(testRoom.getId(), testCustomer.getId(), start.plusDays(1), end.plusDays(1)));

        assertTrue(ex.getMessage().contains("inte tillgängligt"));
    }



    @Test
    void deleteBookingWorks() {
        LocalDate start = LocalDate.now().plusDays(5);
        LocalDate end = start.plusDays(2);

        Booking created = bookingService.createBooking(testRoom.getId(), testCustomer.getId(), start, end);
        Long bookingId = created.getId();

        bookingService.deleteBooking(bookingId);

        assertFalse(bookingRepository.findById(bookingId).isPresent());
    }

    @Test
    void updateBookingChangesDates() {
        LocalDate start = LocalDate.now().plusDays(2);
        LocalDate end = start.plusDays(2);

        Booking original = bookingService.createBooking(testRoom.getId(), testCustomer.getId(), start, end);

        BookingDTO dto = new BookingDTO(
                original.getId(),
                testCustomer.getId(),
                testRoom.getId(),
                start.plusDays(2),
                end.plusDays(3)
        );

        bookingService.updateBooking(dto);
        Booking updated = bookingRepository.findById(original.getId()).orElseThrow();

        assertEquals(start.plusDays(2), updated.getStartDate());
        assertEquals(end.plusDays(3), updated.getEndDate());
    }
}
