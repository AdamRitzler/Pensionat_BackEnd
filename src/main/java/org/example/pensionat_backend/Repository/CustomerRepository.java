package org.example.pensionat_backend.Repository;

import org.example.pensionat_backend.Models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Integer> {
}
