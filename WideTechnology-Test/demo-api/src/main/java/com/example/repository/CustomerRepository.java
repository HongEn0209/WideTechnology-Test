package com.example.repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
	
	// Built-in pagination for customers
    Page<Customer> findAll(Pageable pageable);

    // Find customer by ID
    Optional<Customer> findById(Long id);
    
    // Find customer by phoneNumber
    Optional<Customer> findByPhoneNumber(String phoneNumber);
}
