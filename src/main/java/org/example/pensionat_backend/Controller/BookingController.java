package org.example.pensionat_backend.Controller;

import org.example.pensionat_backend.DTO.BookingDTO;
import org.example.pensionat_backend.Models.Room;
import org.example.pensionat_backend.Service.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.example.pensionat_backend.Service.BookingService;

import java.time.LocalDate;


@Controller
@RequestMapping("/book")
public class BookingController {

    private final RoomService roomService;
    private final BookingService bookingService;

    public BookingController(RoomService roomService, BookingService bookingService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    @GetMapping("/start")
    public String showBookingForm(@RequestParam Long roomId, Model model) {
        Room room = roomService.findById(roomId).orElseThrow();
        model.addAttribute("room", room);
        return "bookingForm"; // vi skapar denna strax
    }

    @PostMapping("/create")
    public String createBooking(
            @RequestParam Long customerId,
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model
    ) {
        BookingDTO dto = new BookingDTO(null, customerId, roomId, startDate, endDate);

        try {
            bookingService.createBooking(dto);
            model.addAttribute("message", "✅ Bokning skapad!");
        } catch (RuntimeException e) {
            model.addAttribute("message", "❌ " + e.getMessage());
        }

        return "bookingConfirmation";
    }

}
