package com.hotel.controller;

import com.hotel.model.Room;
import com.hotel.service.RoomService;
import com.hotel.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BillingController {

    @FXML
    private ComboBox<Room> roomComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField serviceChargeField;

    @FXML
    private Label daysLabel;

    @FXML
    private Label selectedRoomLabel;

    @FXML
    private Label totalBillLabel;

    private final RoomService roomService = new RoomService();

    @FXML
    public void initialize() {
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
        String serviceChargeText = serviceChargeField.getText().trim();
        long calculatedDays = calculateDays();

        if (selectedRoom == null || calculatedDays < 1 || serviceChargeText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a room, valid dates, and service charge.");
            return;
        }

        Integer numberOfDays;
        Double serviceCharge;
        Double totalBill;

        try {
            numberOfDays = Integer.valueOf((int) calculatedDays);
            serviceCharge = Double.valueOf(serviceChargeText);
        } catch (NumberFormatException exception) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Service charge must be a valid number.");
            return;
        }

        totalBill = (selectedRoom.getPrice() * numberOfDays) + serviceCharge;

        selectedRoomLabel.setText("Selected room: " + selectedRoom.getRoomNumber() + " (" + selectedRoom.getRoomType() + ")");
        totalBillLabel.setText(String.format("Total Bill: Rs. %.2f", totalBill));
    }

    @FXML
    public void handleDateSelection() {
        long numberOfDays = calculateDays();
        daysLabel.setText(String.valueOf(numberOfDays));
    }

    private long calculateDays() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            return 0;
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        return Math.max(days, 0);
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
