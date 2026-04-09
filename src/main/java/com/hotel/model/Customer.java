package com.hotel.model;

public class Customer {

    private int customerId;
    private String name;
    private String contact;
    private String assignedRoomNumber;
    private Double totalBill;

    public Customer() {
    }

    public Customer(int customerId, String name, String contact, String assignedRoomNumber, Double totalBill) {
        this.customerId = customerId;
        this.name = name;
        this.contact = contact;
        this.assignedRoomNumber = assignedRoomNumber;
        this.totalBill = totalBill;
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

    public Double getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(Double totalBill) {
        this.totalBill = totalBill;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                ", assignedRoomNumber='" + assignedRoomNumber + '\'' +
                ", totalBill=" + totalBill +
                '}';
    }
}
