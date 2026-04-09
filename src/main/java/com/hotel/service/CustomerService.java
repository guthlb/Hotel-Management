package com.hotel.service;

import com.hotel.model.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerService {

    private static final ArrayList<Customer> customers = new ArrayList<>();
    private static int nextCustomerId = 1;

    public void addCustomer(String name, String contact, String assignedRoomNumber) {
        Customer customer = new Customer(nextCustomerId, name, contact, assignedRoomNumber);
        customers.add(customer);
        nextCustomerId++;
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }
}
