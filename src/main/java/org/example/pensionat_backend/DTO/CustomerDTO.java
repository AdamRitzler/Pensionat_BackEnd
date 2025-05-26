package org.example.pensionat_backend.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerDTO {

    private Long id;

    @NotBlank(message = "Namn måste anges")
    private String name;

    @Email(message = "Ange en giltig e-postadress")
    private String email;

    @Pattern(regexp = "^\\+?\\d{7,15}$", message = "Telefonnummer är ogiltigt")
    private String phone;

    @Pattern(regexp = "^\\d{6}[-]?\\d{4}$", message = "ååmmddxxxx")
    private String ssn;
}
