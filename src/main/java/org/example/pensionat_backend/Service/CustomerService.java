package org.example.pensionat_backend.Service;

import org.example.pensionat_backend.DTO.CustomerDTO;
import org.example.pensionat_backend.Models.Customer;
import org.example.pensionat_backend.Repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    // Repositoryt vi använder för att jobba med kunddata i databasen
    @Autowired
    private CustomerRepository customerRepository;


    // Omvandlar en Customer till en CustomerDTO (så vi inte skickar ut hela entiteten)
    private CustomerDTO toDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setSsn(customer.getSsn());
        return dto;
    }

    //  tvärtom konverterar en DTO till en riktig Customer entity så vi kan spara den i databasen
    private Customer toEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setSsn(dto.getSsn());
        return customer;
    }

    // Hämtar alla kunder och returnerar dem som DTO lista
    public List<CustomerDTO> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Försöker hämta en kund utifrån ID och returnerar den som DTO om den hittas
    public Optional<CustomerDTO> findById(Long id) {
        return customerRepository.findById(id).map(this::toDTO);
    }

    // Vi Sparar eller uppdaterar en kund i databasen och returnerar den sparade versionen som DTO
    public CustomerDTO save(CustomerDTO dto) {
        Customer customer = toEntity(dto);
        Customer saved = customerRepository.save(customer);
        return toDTO(saved);
    }

    // Tar bort en kund baserat på ID, returnerar true om det gick bra
    public boolean deleteById(Long id) {
        customerRepository.deleteById(id);
        return true;
    }

    // Kollar om en viss kund har någon bokning eller inte
    public boolean customerHasBooking(Long id) {
        return customerRepository.findById(id)
                .map(c -> c.getBookings() != null && !c.getBookings().isEmpty())
                .orElse(false);
    }

    // Hämtar alla kunder som har minst en bokning
    public List<Customer> findCustomersWithBookings() {
        return customerRepository.findAll().stream()
                .filter(c -> c.getBookings() != null && !c.getBookings().isEmpty())
                .toList();
    }

}
