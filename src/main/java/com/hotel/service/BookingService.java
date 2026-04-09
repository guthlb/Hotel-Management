package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import com.hotel.util.DatabaseConnection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    private static final String BOOKING_LOG_FILE = "bookings.txt";

    private final RoomService roomService = new RoomService();
    private final CustomerService customerService = new CustomerService();

    public boolean createBooking(Customer customer, Room room) {
        return createBooking(customer, room, LocalDate.now(), LocalDate.now().plusDays(1), 1);
    }

    public boolean createBooking(Customer customer, Room room, LocalDate startDate, LocalDate endDate, int numberOfDays) {
        if (customer == null || room == null || !room.isAvailable()) {
            return false;
        }

        String checkBooking = "SELECT COUNT(*) FROM Booking WHERE roomNumber = ? AND status = ?";
        String insertBooking = "INSERT INTO Booking (customerName, roomNumber, status) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkBooking);
             PreparedStatement insertStatement = connection.prepareStatement(insertBooking)) {
            checkStatement.setString(1, room.getRoomNumber());
            checkStatement.setString(2, "Active");

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return false;
                }
            }

            insertStatement.setString(1, customer.getName());
            insertStatement.setString(2, room.getRoomNumber());
            insertStatement.setString(3, "Active");
            insertStatement.executeUpdate();

            Booking booking = new Booking(0, customer, room, startDate, endDate, numberOfDays, 0.0, true);
            saveBookingToFile(booking);
            roomService.updateRoomAvailability(room.getRoomNumber(), false);
            return true;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to create booking in database.", exception);
        }
    }

    public boolean checkoutBooking(Booking booking) {
        if (booking == null || !booking.isActive()) {
            return false;
        }

        String updateBooking = "UPDATE Booking SET status = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateBooking)) {
            statement.setString(1, "Checked Out");
            statement.setInt(2, booking.getBookingId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                roomService.updateRoomAvailability(booking.getRoom().getRoomNumber(), true);
                return true;
            }

            return false;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to checkout booking.", exception);
        }
    }

    public List<Booking> getAllBookings() {
        ArrayList<Booking> bookings = new ArrayList<>();
        List<Customer> customers = customerService.getAllCustomers();
        List<Room> rooms = roomService.getAllRooms();
        String selectBookings = "SELECT id, customerName, roomNumber, status FROM Booking";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectBookings);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String customerName = resultSet.getString("customerName");
                String roomNumber = resultSet.getString("roomNumber");

                Customer customer = findCustomerByName(customers, customerName);
                Room room = findRoomByNumber(rooms, roomNumber);

                if (customer == null || room == null) {
                    continue;
                }

                Booking booking = new Booking(
                        resultSet.getInt("id"),
                        customer,
                        room,
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        1,
                        0.0,
                        "Active".equalsIgnoreCase(resultSet.getString("status"))
                );
                bookings.add(booking);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to load bookings from database.", exception);
        }

        return bookings;
    }

    private Customer findCustomerByName(List<Customer> customers, String customerName) {
        for (Customer customer : customers) {
            if (customer.getName().equalsIgnoreCase(customerName)) {
                return customer;
            }
        }
        return null;
    }

    private Room findRoomByNumber(List<Room> rooms, String roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                return room;
            }
        }
        return null;
    }

    public void saveBookingToFile(Booking booking) {
        try {
            File bookingFile = new File(BOOKING_LOG_FILE);
            if (!bookingFile.exists()) {
                bookingFile.createNewFile();
            }

            try (FileWriter writer = new FileWriter(bookingFile, true)) {
                writer.write("----------------------------------------\n");
                writer.write("Booking Record\n");
                writer.write("Customer Name: " + booking.getCustomer().getName() + "\n");
                writer.write("Room Number: " + booking.getRoom().getRoomNumber() + "\n");
                writer.write("Room Type: " + booking.getRoom().getRoomType() + "\n");
                writer.write("Price per Day: " + booking.getRoom().getPrice() + "\n");
                writer.write("Start Date: " + booking.getCheckInDate().toString() + "\n");
                writer.write("End Date (Checkout): " + booking.getCheckOutDate().toString() + "\n");
                writer.write("Total Days: " + booking.getNumberOfDays() + "\n");
                writer.write("Total Bill: " + booking.calculateTotalBill() + "\n");
                writer.write("----------------------------------------\n");
            }
        } catch (IOException exception) {
            throw new RuntimeException("Unable to save booking to file.", exception);
        }
    }
}
