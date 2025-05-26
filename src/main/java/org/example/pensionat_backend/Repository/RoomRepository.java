package org.example.pensionat_backend.Repository;

import org.example.pensionat_backend.Models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    // RoomRepository.java
    @Query("SELECT r FROM Room r LEFT JOIN FETCH r.bookings WHERE r.id = :id")
    Optional<Room> findByIdWithBookings(@Param("id") Long id);

    @Query("SELECT DISTINCT r FROM Room r LEFT JOIN FETCH r.bookings")
    List<Room> findAllWithBookings();


}
