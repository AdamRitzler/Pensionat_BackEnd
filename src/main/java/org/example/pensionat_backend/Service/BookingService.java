package org.example.pensionat_backend.Service;

import org.example.pensionat_backend.DTO.BookingDTO;
import org.example.pensionat_backend.Models.Booking;
import org.example.pensionat_backend.Models.Room;
import org.example.pensionat_backend.Repository.BookingRepository;
import org.example.pensionat_backend.Repository.CustomerRepository;
import org.example.pensionat_backend.Repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;

    @Autowired
    private RoomService roomService; // Autowired RoomService, se till att den finns

    public BookingService(BookingRepository bookingRepository, CustomerRepository customerRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.roomRepository = roomRepository;
    }

    // Hämta alla bokningar som DTO
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Hämta en bokning som DTO via ID
    public BookingDTO getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Bokning hittades inte"));
    }

    // Skapa bokning (void-version, kan användas om du inte vill returnera bokning)
    public void createBooking(BookingDTO dto) {
        if (!isRoomAvailable(dto.getRoomId(), dto.getStartDate(), dto.getEndDate())) {
            throw new RuntimeException("Rummet är inte tillgängligt för vald period.");
        }

        Booking booking = new Booking();
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setCustomer(customerRepository.findById(dto.getCustomerId()).orElseThrow(() -> new RuntimeException("Kund hittades inte")));
        booking.setRoom(roomRepository.findById(dto.getRoomId()).orElseThrow(() -> new RuntimeException("Rum hittades inte")));

        bookingRepository.save(booking);
    }

    // Skapa bokning OCH returnera sparad Booking (denna ska du använda för bekräftelsevy)
    public Booking createBooking(Long roomId, Long customerId, LocalDate startDate, LocalDate endDate) {
        if (!isRoomAvailable(roomId, startDate, endDate)) {
            throw new RuntimeException("Rummet är inte tillgängligt för vald period.");
        }

        Booking booking = new Booking();
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setCustomer(customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Kund hittades inte")));
        booking.setRoom(roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Rum hittades inte")));

        return bookingRepository.save(booking);
    }

    // Hämta bokning som Optional (kan användas t.ex. vid uppdatering eller visning)
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    // Uppdatera bokning
    public void updateBooking(BookingDTO dto) {
        Booking booking = bookingRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("Bokning hittades inte"));

        if (!isRoomAvailableForUpdate(dto)) {
            throw new RuntimeException("Rummet är redan bokat för det datumet.");
        }

        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setCustomer(customerRepository.findById(dto.getCustomerId()).orElseThrow(() -> new RuntimeException("Kund hittades inte")));
        booking.setRoom(roomRepository.findById(dto.getRoomId()).orElseThrow(() -> new RuntimeException("Rum hittades inte")));

        bookingRepository.save(booking);
    }

    // Ta bort bokning
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    // Kontrollera om rum är tillgängligt för en ny bokning
    public boolean isRoomAvailable(Long roomId, LocalDate startDate, LocalDate endDate) {
        Room room = roomService.findById(roomId).orElseThrow(() -> new RuntimeException("Rum hittades inte"));
        List<Booking> bookings = room.getBookings();

        // Ingen bokning ska överlappa datumintervallet
        return bookings.stream().noneMatch(b ->
                !(endDate.isBefore(b.getStartDate()) || startDate.isAfter(b.getEndDate()))
        );
    }

    // Kontrollera om rum är tillgängligt för uppdatering av bokning (exkluderar aktuell bokning)
    public boolean isRoomAvailableForUpdate(BookingDTO dto) {
        Room room = roomService.findById(dto.getRoomId()).orElseThrow(() -> new RuntimeException("Rum hittades inte"));
        List<Booking> bookings = room.getBookings();

        return bookings.stream()
                .filter(b -> !b.getId().equals(dto.getId())) // exkludera aktuell bokning
                .noneMatch(b ->
                        !(dto.getEndDate().isBefore(b.getStartDate()) || dto.getStartDate().isAfter(b.getEndDate()))
                );
    }

    // Hämta en bokning för kund + rum + exakt datum (om du vill)
    public Optional<Booking> getLatestBookingForCustomerAndRoom(Long customerId, Long roomId, LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getCustomer().getId().equals(customerId))
                .filter(b -> b.getRoom().getId().equals(roomId))
                .filter(b -> b.getStartDate().equals(startDate) && b.getEndDate().equals(endDate))
                .findFirst();
    }

    // Hjälpmetod: konvertera Booking till BookingDTO
    private BookingDTO convertToDTO(Booking booking) {
        return new BookingDTO(
                booking.getId(),
                booking.getCustomer().getId(),
                booking.getRoom().getId(),
                booking.getStartDate(),
                booking.getEndDate()
        );
    }




}
