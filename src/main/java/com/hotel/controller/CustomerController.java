package com.hotel.controller;

import com.hotel.model.Customer;
import com.hotel.service.CustomerService;
import com.hotel.util.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomerController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField contactField;

    @FXML
    private TextField roomAssignedField;

    @FXML
    private TableView<Customer> customerTableView;

    @FXML
    private TableColumn<Customer, Integer> customerIdColumn;

    @FXML
    private TableColumn<Customer, String> nameColumn;

    @FXML
    private TableColumn<Customer, String> contactColumn;

    @FXML
    private TableColumn<Customer, String> roomAssignedColumn;

    private final ObservableList<Customer> customerObservableList = FXCollections.observableArrayList();
    private final CustomerService customerService = new CustomerService();

    @FXML
    public void initialize() {
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        roomAssignedColumn.setCellValueFactory(new PropertyValueFactory<>("assignedRoomNumber"));

        customerTableView.setItems(customerObservableList);
        loadCustomers();
    }

    @FXML
    public void handleAddCustomer() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String roomAssigned = roomAssignedField.getText().trim();

        if (name.isEmpty() || contact.isEmpty() || roomAssigned.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all customer details.");
            return;
        }

        customerService.addCustomer(name, contact, roomAssigned);
        loadCustomers();

        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Customer added successfully.");
    }

    private void loadCustomers() {
        customerObservableList.setAll(customerService.getAllCustomers());
    }

    private void clearForm() {
        nameField.clear();
        contactField.clear();
        roomAssignedField.clear();
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
