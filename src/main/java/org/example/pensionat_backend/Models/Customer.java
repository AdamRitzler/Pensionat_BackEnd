package org.example.pensionat_backend.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Customer {
        @Id
        @GeneratedValue
        private Long id;
        private String name;
        private String email;
        private String phone;
    }