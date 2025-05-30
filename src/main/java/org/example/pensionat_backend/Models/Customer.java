package org.example.pensionat_backend.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "customer",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name", name = "uk_customer_name"),
                @UniqueConstraint(columnNames = "email", name = "uk_customer_email"),
                @UniqueConstraint(columnNames = "phone", name = "uk_customer_phone"),
                @UniqueConstraint(columnNames = "ssn", name = "uk_customer_ssn")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Namn måste anges")
    @Column(unique = true)
    private String name;

    @Email(message = "Ogiltig emailadress")
    @NotBlank(message = "Email måste anges")
    @Column(unique = true)
    private String email;

    @Pattern(regexp = "^\\d{7,15}$", message = "Telefonnummer måste bestå av 7 till 15 siffror")
    @Column(unique = true)
    private String phone;

    @Pattern(regexp = "^\\d{6}[-]?\\d{4}$", message = "ååmmddxxxx")
    @NotBlank(message = "Personnummer får inte vara tomt")
    @Column(unique = true)
    private String ssn;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();


    public Customer(Object o, String name, String mail, String number, String ssn) {
        this.name = name;
        this.email = mail;
        this.phone = number;
        this.ssn = ssn;

    }
}
