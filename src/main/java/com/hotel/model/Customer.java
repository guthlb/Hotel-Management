package com.hotel.model;

public class Customer {

    private int customerId;
    private String name;
    private String contact;
    private String assignedRoomNumber;

    public Customer() {
    }

    public Customer(int customerId, String name, String contact, String assignedRoomNumber) {
        this.customerId = customerId;
        this.name = name;
        this.contact = contact;
        this.assignedRoomNumber = assignedRoomNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAssignedRoomNumber() {
        return assignedRoomNumber;
    }

    public void setAssignedRoomNumber(String assignedRoomNumber) {
        this.assignedRoomNumber = assignedRoomNumber;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                ", assignedRoomNumber='" + assignedRoomNumber + '\'' +
                '}';
    }
}
