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

    @Autowired
    private CustomerRepository customerRepository;

    // Konverterar entity till DTO
    private CustomerDTO toDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setSsn(customer.getSsn());
        return dto;
    }

    // Konverterar DTO till entity
    private Customer toEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setSsn(dto.getSsn());
        return customer;
    }

    public List<CustomerDTO> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<CustomerDTO> findById(Long id) {
        return customerRepository.findById(id).map(this::toDTO);
    }

    public CustomerDTO save(CustomerDTO dto) {
        Customer customer = toEntity(dto);
        Customer saved = customerRepository.save(customer);
        return toDTO(saved);
    }

    public boolean deleteById(Long id) {
        customerRepository.deleteById(id);
        return true;
    }

}
