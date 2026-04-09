package com.hotel.controller;

import com.hotel.model.Room;
import com.hotel.service.RoomService;
import com.hotel.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.List;

public class BillingController {

    @FXML
    private ComboBox<Room> roomComboBox;

    @FXML
    private TextField daysField;

    @FXML
    private TextField serviceChargeField;

    @FXML
    private Label selectedRoomLabel;

    @FXML
    private Label totalBillLabel;

    private final RoomService roomService = new RoomService();

    @FXML
    public void initialize() {
        roomService.loadFromFile();
        List<Room> rooms = roomService.getAllRooms();
        roomComboBox.getItems().setAll(rooms);

        roomComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Room room) {
                if (room == null) {
                    return "";
                }
                return room.getRoomNumber() + " - " + room.getRoomType();
            }

            @Override
            public Room fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    public void calculateBill() {
        Room selectedRoom = roomComboBox.getValue();
        String daysText = daysField.getText().trim();
        String serviceChargeText = serviceChargeField.getText().trim();

        if (selectedRoom == null || daysText.isEmpty() || serviceChargeText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a room and enter all values.");
            return;
        }

        Integer numberOfDays;
        Double serviceCharge;
        Double totalBill;

        try {
            numberOfDays = Integer.valueOf(daysText);
            serviceCharge = Double.valueOf(serviceChargeText);
        } catch (NumberFormatException exception) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Days and service charge must be valid numbers.");
            return;
        }

        totalBill = (selectedRoom.getPrice() * numberOfDays) + serviceCharge;

        selectedRoomLabel.setText("Selected room: " + selectedRoom.getRoomNumber() + " (" + selectedRoom.getRoomType() + ")");
        totalBillLabel.setText(String.format("Total Bill: Rs. %.2f", totalBill));
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
