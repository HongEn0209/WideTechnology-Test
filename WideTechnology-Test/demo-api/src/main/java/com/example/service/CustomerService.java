package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.model.Account;
import com.example.model.Customer;
import com.example.repository.AccountRepository;
import com.example.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Customer createCustomer(Customer customer) {
        // Ensure account is linked properly
        if (customer.getAccount() != null) {
            Account account = customer.getAccount();
            account.setAccountId(null); // Ensure account ID is not manually assigned
        }
        return customerRepository.save(customer);
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Page<Customer> getAllCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findAll(pageable);  // Returns a Page of customers
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        
        if (customerOptional.isPresent()) {
            Customer existingCustomer = customerOptional.get();
            
            // Update the basic customer details
            existingCustomer.setPhoneNumber(customerDetails.getPhoneNumber());
            existingCustomer.setEmail(customerDetails.getEmail());
            existingCustomer.setAddress(customerDetails.getAddress());
            
            // If the account exists, update the account
            if (customerDetails.getAccount() != null) {
                // Ensure the account id is linked to the existing customer
                Account existingAccount = existingCustomer.getAccount();
                if (existingAccount != null) {
                    // Update the account details if necessary
                    existingAccount.setAccountNumber(customerDetails.getAccount().getAccountNumber());
                    existingAccount.setAccountType(customerDetails.getAccount().getAccountType());
                    existingAccount.setAccountBalance(customerDetails.getAccount().getAccountBalance());
                } else {
                    // If no account is linked, set the new account
                    existingCustomer.setAccount(customerDetails.getAccount());
                }
            }

            // Save the updated customer (this will also persist changes to the account if cascade is set)
            return customerRepository.save(existingCustomer);
        }
        
        return null; // Or throw an exception if not found
    }

    public boolean deleteCustomer(Long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        if (customerOptional.isPresent()) {
            customerRepository.delete(customerOptional.get());
            return true;
        }
        return false;
    }
}
