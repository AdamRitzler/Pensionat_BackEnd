package org.example.pensionat_backend;

import org.example.pensionat_backend.Models.Booking;
import org.example.pensionat_backend.Models.Customer;
import org.example.pensionat_backend.Models.Room;
import org.example.pensionat_backend.Models.RoomType;
import org.example.pensionat_backend.Repository.BookingRepository;
import org.example.pensionat_backend.Repository.CustomerRepository;
import org.example.pensionat_backend.Repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class TestDataConfig {
    @Bean
    CommandLineRunner loadTestData(
            RoomRepository roomRepository,
            CustomerRepository customerRepository,
            BookingRepository bookingRepository
    ) {
        return args -> {
            // Kontroll – om det redan finns rum, hoppa över allt
            if (roomRepository.count() > 0 || customerRepository.count() > 0 || bookingRepository.count() > 0) {
                System.out.println("Testdata redan finns – hoppar över inladdning.");
                return;
            }

            Room room1 = new Room(null, "101", RoomType.SINGLE, 0, true, null);
            Room room2 = new Room(null, "202", RoomType.DOUBLE, 2, true, null);
            Room room3 = new Room(null, "203", RoomType.DOUBLE, 2, true, null);
            Room room4 = new Room(null, "204", RoomType.SINGLE, 0, true, null);
            Room room5 = new Room(null, "205", RoomType.DOUBLE, 1, true, null);
            Room room6 = new Room(null, "206", RoomType.SINGLE, 0, true, null);
            roomRepository.saveAll(List.of(room1, room2));

            Customer customer = new Customer(null, "Emma Test", "emma@mail.com", "0756799473", "9805264559");
            customerRepository.save(customer);

            Booking booking = new Booking(null,
                    LocalDate.of(2025,6,5),
                    LocalDate.of(2025,6,10),
                    customer,
                    room2);
            bookingRepository.save(booking);

            System.out.println("✅ Testdata laddad: 2 rum, 1 kund, 1 bokning.");
        };
    }   }
