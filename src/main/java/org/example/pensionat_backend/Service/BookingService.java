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

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;

    public BookingService(BookingRepository bookingRepository, CustomerRepository customerRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.roomRepository = roomRepository;
    }

    // Hämta alla bokningar
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Hämta bokning via ID
    public BookingDTO getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Bokning hittades inte"));
    }

    // Skapa bokning
    public void createBooking(BookingDTO dto) {
        if (!isRoomAvailable(dto.getRoomId(), dto.getStartDate(), dto.getEndDate())) {
            throw new RuntimeException("Rummet är inte tillgängligt för vald period.");
        }

        Booking booking = new Booking();
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setCustomer(customerRepository.findById(dto.getCustomerId()).orElseThrow());
        booking.setRoom(roomRepository.findById(dto.getRoomId()).orElseThrow());

        bookingRepository.save(booking);
    }

    // Uppdatera bokning
    public void updateBooking(BookingDTO dto) {
        Booking booking = bookingRepository.findById(dto.getId()).orElseThrow();

        if (!isRoomAvailableForUpdate(dto)) {
            throw new RuntimeException("Rummet är redan bokat för det datumet.");
        }

        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setCustomer(customerRepository.findById(dto.getCustomerId()).orElseThrow());
        booking.setRoom(roomRepository.findById(dto.getRoomId()).orElseThrow());

        bookingRepository.save(booking);
    }

    // Radera bokning
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    @Autowired
    private RoomService roomService; // Se till att detta finns

    public boolean isRoomAvailable(Long roomId, LocalDate startDate, LocalDate endDate) {
        Room room = roomService.findById(roomId).orElseThrow();
        List<Booking> bookings = room.getBookings();

        return bookings.stream().noneMatch(b ->
                !(endDate.isBefore(b.getStartDate()) || startDate.isAfter(b.getEndDate()))
        );
    }


    public boolean isRoomAvailableForUpdate(BookingDTO dto) {
        Room room = roomService.findById(dto.getRoomId()).orElseThrow();
        List<Booking> bookings = room.getBookings();

        return bookings.stream()
                .filter(b -> !b.getId().equals(dto.getId())) // exkludera bokningen vi ska uppdatera
                .noneMatch(b ->
                        !(dto.getEndDate().isBefore(b.getStartDate()) || dto.getStartDate().isAfter(b.getEndDate()))
                );
    }


    // Konvertera till DTO
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
