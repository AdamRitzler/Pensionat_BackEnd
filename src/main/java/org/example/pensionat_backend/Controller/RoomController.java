package org.example.pensionat_backend.Controller;

import org.example.pensionat_backend.Models.Room;
import org.example.pensionat_backend.Service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public String showAllrooms(Model model) {
        List<Room> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);
        return "rooms";
    }

    @GetMapping("/search")
    public String showSearchForm() {
        return "searchRooms";
    }

    @GetMapping("/search/results")
    public String showAvailableRooms(
            @RequestParam int guests,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            Model model) {

        if (checkIn.isBefore(LocalDate.now()) || checkOut.isBefore(checkIn)) {
            model.addAttribute("error", "Ogiltigt datumintervall.");
            return "searchRooms";
        }


        List<Room> available = roomService.findAvailableRoomsFor(guests, checkIn, checkOut);
        model.addAttribute("rooms", available);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("guests", guests);

        return "rooms";
    }

}
