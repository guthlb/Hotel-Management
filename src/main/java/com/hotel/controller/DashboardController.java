package com.hotel.controller;

import com.hotel.model.Room;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import com.hotel.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;

public class DashboardController {

    @FXML
    private Label totalRoomsLabel;

    @FXML
    private Label availableRoomsLabel;

    @FXML
    private Label totalCustomersLabel;

    private final RoomService roomService = new RoomService();
    private final CustomerService customerService = new CustomerService();

    @FXML
    public void initialize() {
        loadDashboardData();
    }

    public void handleRefresh(ActionEvent event) {
        loadDashboardData();
        System.out.println("Refreshed");
    }

    private void loadDashboardData() {
        roomService.loadFromFile();

        ArrayList<Room> allRooms = new ArrayList<>(roomService.getAllRooms());
        ArrayList<Room> availableRooms = new ArrayList<>();

        for (Room room : allRooms) {
            if (room.isAvailable()) {
                availableRooms.add(room);
            }
        }

        int totalRooms = allRooms.size();
        int totalAvailableRooms = availableRooms.size();
        int totalCustomers = new ArrayList<>(customerService.getAllCustomers()).size();

        totalRoomsLabel.setText("Total Rooms: " + totalRooms);
        availableRoomsLabel.setText("Available Rooms: " + totalAvailableRooms);
        totalCustomersLabel.setText("Customers: " + totalCustomers);
    }

    public void openDashboard(ActionEvent event) {
        System.out.println("Dashboard button clicked");
        SceneSwitcher.switchScene(event, "/fxml/dashboard.fxml", "Hotel Management System");
    }

    public void openRooms(ActionEvent event) {
        System.out.println("Rooms button clicked");
        SceneSwitcher.switchScene(event, "/fxml/rooms.fxml", "Room Management");
    }

    public void openCustomers(ActionEvent event) {
        System.out.println("Customers button clicked");
        SceneSwitcher.switchScene(event, "/fxml/customer.fxml", "Customer Management");
    }

    public void openBookings(ActionEvent event) {
        System.out.println("Bookings button clicked");
        SceneSwitcher.switchScene(event, "/fxml/booking.fxml", "Booking Management");
    }

    public void openBilling(ActionEvent event) {
        System.out.println("Billing button clicked");
        SceneSwitcher.switchScene(event, "/fxml/billing.fxml", "Billing");
    }
}
