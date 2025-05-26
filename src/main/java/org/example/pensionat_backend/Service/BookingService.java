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
    private RoomService roomService;

    // Konstruktor där vi injicerar repositories
    public BookingService(BookingRepository bookingRepository, CustomerRepository customerRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.roomRepository = roomRepository;
    }


    // Hämtar alla bokningar och konverterar dem till BookingDTO
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // Hämtar en specifik bokning med ID och konverterar till DTO
    public BookingDTO getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Bokning hittades inte"));
    }


    // Skapar en ny bokning efter att ha validerat datum och tillgängligheten
    public Booking createBooking(Long roomId, Long customerId, LocalDate startDate, LocalDate endDate) {


        // Startdatum får inte vara bakåt i tiden
        if (startDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Startdatum kan inte vara i det förflutna.");
        }


        // Slutdatum måste vara efter startdatum
        if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
            throw new RuntimeException("Slutdatum måste vara efter startdatum.");
        }


        // Rummet måste vara ledigt
        if (!isRoomAvailable(roomId, startDate, endDate)) {
            throw new RuntimeException("Rummet är inte tillgängligt för vald period.");
        }

        // Om allt är okej så skapar vi bokningen
        Booking booking = new Booking();
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setCustomer(customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Kund hittades inte")));
        booking.setRoom(roomRepository.findById(roomId).
                orElseThrow(() -> new RuntimeException("Rum hittades inte")));

        return bookingRepository.save(booking);
    }


    // Försöker hitta en bokning med ett visst ID, returnerar tomt om den inte finns
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }


    // Uppdaterar en befintlig bokning med ny information från DTO
    public void updateBooking(BookingDTO dto) {

        if (dto.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Startdatum kan inte vara i det förflutna.");
        }


        if (dto.getEndDate().isBefore(dto.getStartDate()) || dto.getEndDate().equals(dto.getStartDate())) {
            throw new RuntimeException("Slutdatum måste vara efter startdatum.");
        }


        if (!isRoomAvailableForUpdate(dto)) {
            throw new RuntimeException("Rummet är redan bokat för det datumet.");
        }

        // Hämtar och uppdaterar bokningen
        Booking booking = bookingRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Bokning hittades inte"));

        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setCustomer(customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Kund hittades inte")));
        booking.setRoom(roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Rum hittades inte")));

        bookingRepository.save(booking);
    }


    // Tar bort en bokning med ID
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }


    // Kollar om ett rum är ledigt mellan två datum (används vid ny bokning)
    public boolean isRoomAvailable(Long roomId, LocalDate startDate, LocalDate endDate) {
        Room room = roomRepository.findByIdWithBookings(roomId)
                .orElseThrow(() -> new RuntimeException("Rum hittades inte"));

        List<Booking> bookings = room.getBookings();
        System.out.println("Antal bokningar i rummet: " + bookings.size()); // Debug

        // Returnerar true om inga bokningar överlappar
        return bookings.stream().noneMatch(b ->
                !(endDate.isBefore(b.getStartDate()) || startDate.isAfter(b.getEndDate()))
        );
    }


    // Kollar om rummet är ledigt vid uppdatering (ignorerar sin egen bokning)
    public boolean isRoomAvailableForUpdate(BookingDTO dto) {
        Room room = roomService.findById(dto.getRoomId()).orElseThrow(() -> new RuntimeException("Rum hittades inte"));
        List<Booking> bookings = room.getBookings();

        return bookings.stream()
                .filter(b -> !b.getId().equals(dto.getId()))
                .noneMatch(b ->
                        !(dto.getEndDate().isBefore(b.getStartDate()) || dto.getStartDate().isAfter(b.getEndDate()))
                );
    }


    // Hämtar senaste bokningen för kund och rum med samma datum
    public Optional<Booking> getLatestBookingForCustomerAndRoom(Long customerId, Long roomId, LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getCustomer().getId().equals(customerId))
                .filter(b -> b.getRoom().getId().equals(roomId))
                .filter(b -> b.getStartDate().equals(startDate) && b.getEndDate().equals(endDate))
                .findFirst();
    }


    // Vi la till Hjälpmetod för att konvertera Booking till BookingDTO
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
