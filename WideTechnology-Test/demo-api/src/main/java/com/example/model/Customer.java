package com.example.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customer_id;

    private String phoneNumber;
    private String email;
    private String address;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "accountId", referencedColumnName = "accountId") // Foreign key column in Customer table
    private Account account;
    
    public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}
    
}