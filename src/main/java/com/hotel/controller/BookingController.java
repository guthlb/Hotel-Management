package com.hotel.controller;

import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import com.hotel.service.BookingService;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import com.hotel.util.SceneSwitcher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingController {

    @FXML
    private ComboBox<Customer> customerComboBox;

    @FXML
    private ComboBox<Room> roomComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Label bookingDaysLabel;

    @FXML
    private Label roomPriceLabel;

    @FXML
    private TextField serviceChargeField;

    @FXML
    private Label calculatedBillLabel;

    @FXML
    private TableView<Booking> bookingTableView;

    @FXML
    private TableColumn<Booking, Integer> bookingIdColumn;

    @FXML
    private TableColumn<Booking, String> customerNameColumn;

    @FXML
    private TableColumn<Booking, String> roomNumberColumn;

    @FXML
    private TableColumn<Booking, String> statusColumn;

    private final ObservableList<Booking> bookingObservableList = FXCollections.observableArrayList();
    private final CustomerService customerService = new CustomerService();
    private final RoomService roomService = new RoomService();
    private final BookingService bookingService = new BookingService();
    private Double calculatedBillAmount;

    @FXML
    public void initialize() {
        bookingIdColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("bookingId"));
        customerNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCustomer().getName()));
        roomNumberColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoom().getRoomNumber()));
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isActive() ? "Active" : "Checked Out"));

        customerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Customer customer) {
                if (customer == null) {
                    return "";
                }
                return customer.getCustomerId() + " - " + customer.getName();
            }

            @Override
            public Customer fromString(String string) {
                return null;
            }
        });

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

        bookingTableView.setItems(bookingObservableList);
        refreshData();
    }

    @FXML
    public void handleRoomSelection() {
        Room room = roomComboBox.getValue();
        roomPriceLabel.setText(room == null
                ? "Rs. 0.00 per day"
                : String.format("Rs. %.2f per day", room.getPrice()));
        updateBillPreview();
    }

    @FXML
    public void handleCreateBooking() {
        Customer customer = customerComboBox.getValue();
        Room room = roomComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        long numberOfDays = calculateDays();

        if (customer == null || room == null || startDate == null || endDate == null || numberOfDays < 1) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select customer, room, and valid dates.");
            return;
        }

        if (calculatedBillAmount == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please calculate the bill before creating the booking.");
            return;
        }

        customer.setAssignedRoomNumber(room.getRoomNumber());
        customer.setTotalBill(calculatedBillAmount);

        boolean created = bookingService.createBooking(customer, room, startDate, endDate, (int) numberOfDays);
        if (!created) {
            showAlert(Alert.AlertType.ERROR, "Booking Error", "Room is already booked or unavailable.");
            return;
        }

        customerService.updateCustomerBookingDetails(customer.getCustomerId(), room.getRoomNumber(), calculatedBillAmount);
        refreshData();
        customerComboBox.setValue(null);
        roomComboBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        serviceChargeField.clear();
        bookingDaysLabel.setText("0");
        roomPriceLabel.setText("Rs. 0.00 per day");
        calculatedBillAmount = null;
        calculatedBillLabel.setText("Calculated Bill: Rs. 0.00");
        showAlert(Alert.AlertType.INFORMATION, "Success", "Booking created successfully.");
    }

    @FXML
    public void handleDateSelection() {
        long numberOfDays = calculateDays();
        bookingDaysLabel.setText(String.valueOf(numberOfDays));
        updateBillPreview();
    }

    @FXML
    public void calculateBill() {
        Room room = roomComboBox.getValue();
        long numberOfDays = calculateDays();

        if (room == null || numberOfDays < 1) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a room and valid dates before calculating the bill.");
            return;
        }

        double serviceCharge;
        try {
            String serviceChargeText = serviceChargeField.getText().trim();
            serviceCharge = serviceChargeText.isEmpty() ? 0.0 : Double.parseDouble(serviceChargeText);
        } catch (NumberFormatException exception) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Service charge must be a valid number.");
            return;
        }

        calculatedBillAmount = (room.getPrice() * numberOfDays) + serviceCharge;
        calculatedBillLabel.setText(String.format("Calculated Bill: Rs. %.2f", calculatedBillAmount));
    }

    @FXML
    public void handleCheckout() {
        Booking selectedBooking = bookingTableView.getSelectionModel().getSelectedItem();

        if (selectedBooking == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a booking to checkout.");
            return;
        }

        boolean checkedOut = bookingService.checkoutBooking(selectedBooking);
        if (!checkedOut) {
            showAlert(Alert.AlertType.ERROR, "Checkout Error", "This booking is already checked out.");
            return;
        }

        roomService.updateRoomAvailability(selectedBooking.getRoom().getRoomNumber(), true);
        refreshData();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Customer checkout completed.");
    }

    private void refreshData() {
        customerComboBox.setItems(FXCollections.observableArrayList(customerService.getAllCustomers()));
        roomComboBox.setItems(FXCollections.observableArrayList(roomService.getAllRooms().stream()
                .filter(Room::isAvailable)
                .toList()));
        bookingObservableList.setAll(bookingService.getAllBookings());
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

    private void updateBillPreview() {
        calculatedBillAmount = null;
        calculatedBillLabel.setText("Calculated Bill: Rs. 0.00");
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
}
