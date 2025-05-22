package org.example.pensionat_backend.DTO;

import lombok.Data;

@Data
public class RoomDTO {
    private Long id;
    private String roomNumber;
    private int beds;
    private boolean available;
}