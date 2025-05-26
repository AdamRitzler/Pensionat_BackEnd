package org.example.pensionat_backend.Controller;

import jakarta.validation.Valid;
import org.example.pensionat_backend.DTO.BookingDTO;
import org.example.pensionat_backend.Models.Booking;
import org.example.pensionat_backend.Models.Customer;
import org.example.pensionat_backend.Models.Room;
import org.example.pensionat_backend.Service.CustomerService;
import org.example.pensionat_backend.Service.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.example.pensionat_backend.Service.BookingService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;


@Controller
@RequestMapping("/book")
public class BookingController {

    private final RoomService roomService;
    private final BookingService bookingService;
    private final CustomerService customerService;

    public BookingController(RoomService roomService, BookingService bookingService, CustomerService customerService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.customerService = customerService;
    }

    @GetMapping("/start")
    public String showBookingForm(@RequestParam Long roomId, Model model) {
        Room room = roomService.findById(roomId).orElse(null);
        model.addAttribute("room", room);
        return "bookingForm";
    }


    @PostMapping("/create")
    public String createBooking(
            @RequestParam Long customerId,
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            RedirectAttributes redirectAttributes,Model model
    ) {
        // Validering
        if (startDate.isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("message", "❌ Startdatum kan inte vara i det förflutna.");
            return "redirect:/book/start?roomId=" + roomId;
        }

        if (!endDate.isAfter(startDate)) {
            redirectAttributes.addFlashAttribute("message", "❌ Slutdatum måste vara efter startdatum.");
            return "redirect:/book/start?roomId=" + roomId;
        }

        try {
            Booking booking = bookingService.createBooking(roomId, customerId, startDate, endDate);
            model.addAttribute("room", roomService.findById(roomId).orElse(null));
            return "bookingConfirmation"; // eller annan vy du använder
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", "❌ " + e.getMessage());
            return "redirect:/book/start?roomId=" + roomId;
        }
    }



    @GetMapping("/cancel")
    public String showCustomersWithBookings(Model model) {
        List<Customer> customersWithBookings = customerService.findCustomersWithBookings();
        model.addAttribute("customers", customersWithBookings);
        return "cancelBooking";
    }

    @PostMapping("/cancel")
    public String cancelBooking(@RequestParam Long bookingId, RedirectAttributes redirectAttributes) {
        try {
            bookingService.deleteBooking(bookingId);
            redirectAttributes.addFlashAttribute("message", "Bokningen avbokades.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Kunde inte avboka: " + e.getMessage());
        }
        return "redirect:/book/cancel";

    }


    @PostMapping("/edit")
    public String saveBookingUpdate(
            @Valid @ModelAttribute("booking") BookingDTO booking,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "❌ Formuläret innehåller fel. Försök igen.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/book/edit/" + booking.getId();  // Gå tillbaka till formuläret
        }
        try {
            bookingService.updateBooking(booking);

            redirectAttributes.addFlashAttribute("message", "✅ Bokningen har uppdaterats.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "❌ Fel: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/book/edit/" + booking.getId();  // Gå tillbaka till formuläret med fel
        }

        return "redirect:/book/edit"; // Gå till listan
    }

    @GetMapping("/edit")
    public String showEditBookingList(Model model) {
        List<BookingDTO> bookings = bookingService.getAllBookings();
        model.addAttribute("bookings", bookings);
        return "editBookingList";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        BookingDTO booking = bookingService.getBookingById(id);
        model.addAttribute("booking", booking);
        return "editBookingForm";
    }


}



