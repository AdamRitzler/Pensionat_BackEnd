package org.example.pensionat_backend.Repository;

import org.example.pensionat_backend.Models.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}
