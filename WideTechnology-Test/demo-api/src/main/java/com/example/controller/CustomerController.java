package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.model.Customer;
import com.example.service.CustomerService;
import com.example.exception.CustomerNotFoundException;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/getalldetails")
    public Page<Customer> getAllDetails(@RequestHeader int page, @RequestHeader int size) {
        return customerService.getAllCustomers(page, size);
    }

    @GetMapping("/getdetailById")
    public ResponseEntity<Customer> getDetailById(@RequestHeader Long id) {
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @PostMapping("/create")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @PutMapping("/update")
    public ResponseEntity<Customer> updateCustomer(@RequestHeader Long id, @RequestBody Customer customer) {
        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCustomer(@RequestHeader Long id) {
        boolean deleted = customerService.deleteCustomer(id);
        if (!deleted) {
            throw new CustomerNotFoundException("Cannot delete. Customer with ID " + id + " not found.");
        }
        return ResponseEntity.noContent().build();
    }
}
