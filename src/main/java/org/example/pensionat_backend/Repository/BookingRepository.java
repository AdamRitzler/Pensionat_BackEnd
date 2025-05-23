package org.example.pensionat_backend.Repository;

import org.example.pensionat_backend.Models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookingRepository extends JpaRepository<Booking, Long> {

}
