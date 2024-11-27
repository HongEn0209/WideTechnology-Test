package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	
	// Find Account by Account Number
	Optional<Account> findByAccountNumber(String accountNumber);
}
