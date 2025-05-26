package org.example.pensionat_backend.Service;

import org.example.pensionat_backend.DTO.CustomerDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    private Customer testCustomer;

    @BeforeEach
    void setup() {
        testCustomer = new Customer();
        testCustomer.setName("Testkund");
        testCustomer.setEmail("test@example.com");
        testCustomer.setPhone("0701234567");
        testCustomer.setSsn("900101-1234");
        customerRepository.save(testCustomer);
    }

    @Test
    void findAll() {
        List<CustomerDTO> all = customerService.findAll();
        assertFalse(all.isEmpty());
        assertTrue(all.stream().anyMatch(c -> c.getEmail().equals("test@example.com")));
    }

    @Test
    void findById() {
        CustomerDTO dto = customerService.findById(testCustomer.getId()).orElse(null);
        assertNotNull(dto);
        assertEquals("Testkund", dto.getName());
    }

    @Test
    void save() {
        CustomerDTO newDto = new CustomerDTO();
        newDto.setName("Ny Kund");
        newDto.setEmail("ny@example.com");
        newDto.setPhone("0700000000");
        newDto.setSsn("850505-4321");

        CustomerDTO saved = customerService.save(newDto);

        assertNotNull(saved.getId());
        assertEquals("Ny Kund", saved.getName());
    }

    @Test
    void deleteById() {
        Long id = testCustomer.getId();
        assertTrue(customerService.deleteById(id));
        assertFalse(customerRepository.findById(id).isPresent());
    }

    @Test
    void customerHasBooking() {
        Room room = new Room();
        room.setRoomNumber("R101");
        room.setRoomType(RoomType.SINGLE);
        room.setMaxExtraBeds(0);
        room.setAvailable(true);
        roomRepository.save(room);

        testCustomer = customerRepository.save(testCustomer);

        Booking booking = new Booking();
        booking.setCustomer(testCustomer);
        booking.setRoom(room);
        booking.setStartDate(LocalDate.now().plusDays(1));
        booking.setEndDate(LocalDate.now().plusDays(3));
        bookingRepository.save(booking);

        testCustomer.getBookings().add(booking);
        customerRepository.save(testCustomer);

        bookingRepository.flush(); // 🧠 Skriv till databasen

        // 🧠 Läs om kunden så Hibernate hinner ladda bokningar
        testCustomer = customerRepository.findById(testCustomer.getId()).orElseThrow();
        System.out.println("Antal bokningar: " + testCustomer.getBookings().size());

        boolean hasBooking = customerService.customerHasBooking(testCustomer.getId());

        assertTrue(hasBooking);
    }



    @Test
    void findCustomersWithBookings() {
        Room room = new Room();
        room.setRoomNumber("R202");
        room.setRoomType(RoomType.DOUBLE);
        room.setMaxExtraBeds(2);
        roomRepository.save(room);

        Booking booking = new Booking();
        booking.setCustomer(testCustomer);
        booking.setRoom(room);
        booking.setStartDate(LocalDate.now().plusDays(1));
        booking.setEndDate(LocalDate.now().plusDays(3));

        // 👇 Synka relationen från Customer-hållet
        testCustomer.getBookings().add(booking);

        // 👇 Spara båda – eftersom Cascade.ALL finns på Customer
        customerRepository.save(testCustomer);

        List<Customer> result = customerService.findCustomersWithBookings();

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(testCustomer.getId())));
    }

}
