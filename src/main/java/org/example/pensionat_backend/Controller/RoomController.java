package org.example.pensionat_backend.Controller;

import org.example.pensionat_backend.Models.Room;
import org.example.pensionat_backend.Service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
