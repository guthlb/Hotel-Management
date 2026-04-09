package com.hotel.service;

import com.hotel.model.Customer;
import com.hotel.util.DatabaseConnection;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerService {

    private final ArrayList<Customer> customers = new ArrayList<>();

    public void addCustomer(String name, String contact, String assignedRoomNumber, Double totalBill) {
        String insertCustomer = "INSERT INTO Customer (name, contact, roomAssigned, totalBill) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertCustomer)) {
            statement.setString(1, name);
            statement.setString(2, contact);
            statement.setString(3, assignedRoomNumber);
            statement.setDouble(4, totalBill);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to add customer to database.", exception);
        }
    }

    public List<Customer> getAllCustomers() {
        customers.clear();
        String selectCustomers = "SELECT rowid, name, contact, roomAssigned, totalBill FROM Customer";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectCustomers);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Customer customer = new Customer(
                        resultSet.getInt("rowid"),
                        resultSet.getString("name"),
                        resultSet.getString("contact"),
                        resultSet.getString("roomAssigned"),
                        resultSet.getDouble("totalBill")
                );
                customers.add(customer);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to load customers from database.", exception);
        }

        return new ArrayList<>(customers);
    }

    public void updateCustomerBookingDetails(int customerId, String assignedRoomNumber, Double totalBill) {
        String updateCustomer = "UPDATE Customer SET roomAssigned = ?, totalBill = ? WHERE rowid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateCustomer)) {
            statement.setString(1, assignedRoomNumber);
            statement.setDouble(2, totalBill);
            statement.setInt(3, customerId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to update customer booking details.", exception);
        }
    }
}
