package org.example.pensionat_backend.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingDTO {
    private Long id;
    private Long customerId;
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;
}