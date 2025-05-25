package org.example.pensionat_backend.DTO;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BookingDTO {
    @NotNull(message = "ID saknas (obligatoriskt vid uppdatering)")
    private Long id;

    @NotNull(message = "Kund-ID krävs")
    private Long customerId;

    @NotNull(message = "Rum-ID krävs")
    private Long roomId;

    @NotNull(message = "Startdatum krävs")
    @FutureOrPresent(message = "Startdatum kan inte vara i det förflutna")
    private LocalDate startDate;

    @NotNull(message = "Slutdatum krävs")
    @FutureOrPresent(message = "Slutdatum kan inte vara i det förflutna")
    private LocalDate endDate;


}
