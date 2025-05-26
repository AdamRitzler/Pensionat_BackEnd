package org.example.pensionat_backend.Service;

import org.example.pensionat_backend.Models.Room;
import org.example.pensionat_backend.Models.RoomType;
import org.example.pensionat_backend.Repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public List<Room> findAllRooms(){
        return roomRepository.findAll();
    }

    public List<Room> findAvailableRoomsFor(int guests, LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAllWithBookings().stream()
                .filter(room -> {
                    int baseCapacity = (room.getRoomType() == RoomType.SINGLE) ? 1 : 2;
                    int totalCapacity = baseCapacity + room.getMaxExtraBeds();
                    return totalCapacity >= guests;
                })
                .filter(room -> isAvailable(room, checkIn, checkOut))
                .toList();
    }


    private boolean isAvailable(Room room, LocalDate from, LocalDate to) {
        // Lägg till bokningslogik senare
        return room.getBookings().stream().noneMatch(booking ->
                !(booking.getEndDate().isBefore(from) || booking.getStartDate().isAfter(to)));
    }

    // RoomService.java
    public Optional<Room> findById(Long id) {
        return roomRepository.findByIdWithBookings(id);
    }



}
