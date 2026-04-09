package com.hotel.controller;

import com.hotel.model.Room;
import com.hotel.service.RoomService;
import com.hotel.util.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class RoomController {

    @FXML
    private TextField roomNumberField;

    @FXML
    private ComboBox<String> roomTypeComboBox;

    @FXML
    private TextField priceField;

    @FXML
    private CheckBox availabilityCheckBox;

    @FXML
    private TableView<Room> roomTableView;

    @FXML
    private TableColumn<Room, String> roomNumberColumn;

    @FXML
    private TableColumn<Room, String> roomTypeColumn;

    @FXML
    private TableColumn<Room, Double> priceColumn;

    @FXML
    private TableColumn<Room, Boolean> availabilityColumn;

    private final ObservableList<Room> roomObservableList = FXCollections.observableArrayList();
    private final RoomService roomService = new RoomService();

    @FXML
    public void initialize() {
        roomTypeComboBox.setItems(FXCollections.observableArrayList("Single", "Double", "Deluxe", "Suite"));

        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        roomTableView.setItems(roomObservableList);
        loadRooms();
    }

    @FXML
    public void handleAddRoom() {
        String roomNumber = roomNumberField.getText().trim();
        String roomType = roomTypeComboBox.getValue();
        String priceText = priceField.getText().trim();
        boolean available = availabilityCheckBox.isSelected();

        if (roomNumber.isEmpty() || roomType == null || priceText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all room details.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException exception) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Price must be a valid number.");
            return;
        }

        Room room = new Room(roomNumber, roomType, price, available);
        roomService.addRoom(room);
        loadRooms();

        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Room added successfully.");
    }

    private void loadRooms() {
        roomObservableList.setAll(roomService.getAllRooms());
    }

    private void clearForm() {
        roomNumberField.clear();
        roomTypeComboBox.setValue(null);
        priceField.clear();
        availabilityCheckBox.setSelected(true);
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
