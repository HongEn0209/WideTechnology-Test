package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.exception.CustomerNotFoundException; // Custom exception
import com.example.model.Account;
import com.example.model.Customer;
import com.example.repository.AccountRepository;
import com.example.repository.CustomerRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomerService {

	private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AccountRepository accountRepository;

	/**
	 * Create a new customer and save it to the database.
	 * 
	 * @param customer the customer object to be created.
	 * @return the saved customer object.
	 * @throws IllegalArgumentException if the customer object is invalid.
	 */
	@Transactional
	public Customer createCustomer(Customer customer) {
		try {
			if (customer == null) {
				throw new IllegalArgumentException("Customer cannot be null.");
			}
			
			// Phone Number Validation (Can only Digit)
	        String phoneNumber = customer.getPhoneNumber();
	        if (phoneNumber == null || !phoneNumber.matches("\\d+")) {
	            throw new IllegalArgumentException("Phone number must contain only digits.");
	        }

			// Validate phone number
			customerRepository.findByPhoneNumber(customer.getPhoneNumber()).ifPresent(existingCustomer -> {
				throw new IllegalArgumentException("Phone number " + customer.getPhoneNumber() + " is already in use.");
			});

			// Validate account number if provided
			if (customer.getAccount() != null && customer.getAccount().getAccountNumber() != null) {
				
				//Account Number Validation (Can only Digit)
				String accountNumber = customer.getAccount().getAccountNumber();
	            if (!accountNumber.matches("\\d+")) {
	                throw new IllegalArgumentException("Account number must contain only digits.");
	            }
				
				accountRepository.findByAccountNumber(customer.getAccount().getAccountNumber())
						.ifPresent(existingAccount -> {
							throw new IllegalArgumentException("Account number "
									+ customer.getAccount().getAccountNumber() + " is already in use.");
						});
			}

			return customerRepository.save(customer);

		} catch (IllegalArgumentException e) {
			logger.error("Failed to create customer: {}", e.getMessage());
			throw e; // Re-throw to let it propagate to the controller or global exception handler
		} catch (Exception e) {
			logger.error("An unexpected error occurred while creating a customer", e);
			throw new RuntimeException("Failed to create customer due to an internal error."); // Wrap other exceptions
		}
	}

	/**
	 * Retrieve a customer by its ID.
	 * 
	 * @param id the ID of the customer.
	 * @return the customer object.
	 * @throws CustomerNotFoundException if the customer is not found.
	 */
	public Customer getCustomerById(Long id) {
		try {
			if (id == null || id <= 0) {
				logger.error("Invalid customer ID provided: {}", id);
				throw new IllegalArgumentException("Customer ID must be a positive number.");
			}

			logger.info("Fetching customer with ID: {}", id);
			return customerRepository.findById(id).orElseThrow(() -> {
				logger.error("Customer with ID {} not found.", id);
				return new CustomerNotFoundException("Customer with ID " + id + " not found.");
			});

		} catch (IllegalArgumentException | CustomerNotFoundException e) {
			logger.error("Error retrieving customer: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("An unexpected error occurred while retrieving customer", e);
			throw new RuntimeException("Failed to retrieve customer due to an internal error.");
		}
	}

	/**
	 * Retrieve a list of customers.
	 * 
	 * @param page the page number.
	 * @param size the size of the page.
	 * @return a Page containing customers.
	 */
	public Page<Customer> getAllCustomers(int page, int size) {
		try {
			if (page < 0 || size <= 0) {
				logger.error("Invalid pagination parameters: page = {}, size = {}", page, size);
				throw new IllegalArgumentException("Page number must be >= 0 and size must be > 0.");
			}

			Pageable pageable = PageRequest.of(page, size);
			logger.info("Fetching customers with pagination: page = {}, size = {}", page, size);
			return customerRepository.findAll(pageable);

		} catch (IllegalArgumentException e) {
			logger.error("Error in pagination parameters: {}", e.getMessage());
			throw e; // Rethrow for higher-level handling
		} catch (Exception e) {
			logger.error("Unexpected error occurred while retrieving customers", e);
			throw new RuntimeException("Failed to retrieve customers due to an internal error.");
		}
	}

	/**
	 * Update an existing customer's details.
	 * 
	 * @param id the ID of the customer to update.
	 * @param customerDetails the updated customer details.
	 * @return the updated customer object.
	 * @throws CustomerNotFoundException if the customer is not found.
	 */
	@Transactional
	public Customer updateCustomer(Long id, Customer customerDetails) {
		try {
			if (id == null || id <= 0 || customerDetails == null) {
				throw new IllegalArgumentException("Valid customer ID and details must be provided.");
			}

			Customer existingCustomer = customerRepository.findById(id)
					.orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + id + " not found."));
			
			// Phone Number Validation (Can only Digit)
	        String phoneNumber = customerDetails.getPhoneNumber();
	        if (phoneNumber == null || !phoneNumber.matches("\\d+")) {
	            throw new IllegalArgumentException("Phone number must contain only digits.");
	        }

			// Validate phone number (exclude the current customer from the check)
			customerRepository.findByPhoneNumber(customerDetails.getPhoneNumber()).ifPresent(otherCustomer -> {
				if (!otherCustomer.getCustomer_id().equals(existingCustomer.getCustomer_id())) {
					throw new IllegalArgumentException(
							"Phone number " + customerDetails.getPhoneNumber() + " is already in use.");
				}
			});

			//Account Number Validation (Can only Digit)
			String accountNumber = customerDetails.getAccount().getAccountNumber();
            if (!accountNumber.matches("\\d+")) {
                throw new IllegalArgumentException("Account number must contain only digits.");
            }
			
			// Validate account number if provided (exclude the current account from the check)
			if (customerDetails.getAccount() != null && customerDetails.getAccount().getAccountNumber() != null) {
				accountRepository.findByAccountNumber(customerDetails.getAccount().getAccountNumber())
						.ifPresent(existingAccount -> {
							if (!existingAccount.getAccountId().equals(existingCustomer.getAccount().getAccountId())) {
								throw new IllegalArgumentException("Account number "
										+ customerDetails.getAccount().getAccountNumber() + " is already in use.");
							}
						});
			}

			// Update details
			existingCustomer.setPhoneNumber(customerDetails.getPhoneNumber());
			existingCustomer.setEmail(customerDetails.getEmail());
			existingCustomer.setAddress(customerDetails.getAddress());

			if (customerDetails.getAccount() != null) {
				Account accountDetails = customerDetails.getAccount();
				Account existingAccount = existingCustomer.getAccount();

				if (existingAccount != null) {
					existingAccount.setAccountNumber(accountDetails.getAccountNumber());
					existingAccount.setAccountType(accountDetails.getAccountType());
					existingAccount.setAccountBalance(accountDetails.getAccountBalance());
				} else {
					existingCustomer.setAccount(accountDetails);
				}
			}

			return customerRepository.save(existingCustomer);

		} catch (IllegalArgumentException | CustomerNotFoundException e) {
			logger.error("Error updating customer: {}", e.getMessage());
			throw e; // Rethrow expected exceptions
		} catch (Exception e) {
			logger.error("Unexpected error occurred while updating customer", e);
			throw new RuntimeException("Failed to update customer due to an internal error.");
		}
	}

	/**
	 * Delete a customer by its ID.
	 * 
	 * @param id the ID of the customer to delete.
	 * @return true if the customer was deleted successfully.
	 * @throws CustomerNotFoundException if the customer is not found.
	 */
	@Transactional
	public boolean deleteCustomer(Long id) {
		try {
			if (id == null || id <= 0) {
				throw new IllegalArgumentException("Customer ID must be a positive number.");
			}

			logger.info("Attempting to delete customer with ID: {}", id);
			Customer existingCustomer = customerRepository.findById(id).orElseThrow(() -> {
				logger.error("Customer with ID {} not found for deletion.", id);
				return new CustomerNotFoundException("Customer with ID " + id + " not found.");
			});

			customerRepository.delete(existingCustomer);
			logger.info("Customer with ID {} successfully deleted.", id);
			return true;

		} catch (IllegalArgumentException | CustomerNotFoundException e) {
			logger.error("Error deleting customer: {}", e.getMessage());
			throw e; // Rethrow expected exceptions
		} catch (Exception e) {
			logger.error("Unexpected error occurred while deleting customer", e);
			throw new RuntimeException("Failed to delete customer due to an internal error.");
		}
	}

}
