package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.Room;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileStorageService {

    private static final String DATA_FOLDER = "data";
    private static final String ROOMS_FILE = DATA_FOLDER + "/rooms.txt";
    private static final String CUSTOMERS_FILE = DATA_FOLDER + "/customers.txt";
    private static final String BOOKINGS_FILE = DATA_FOLDER + "/bookings.txt";

    public FileStorageService() {
        createDataFiles();
    }

    private void createDataFiles() {
        try {
            File folder = new File(DATA_FOLDER);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            createFileIfMissing(ROOMS_FILE);
            createFileIfMissing(CUSTOMERS_FILE);
            createFileIfMissing(BOOKINGS_FILE);
        } catch (IOException exception) {
            throw new RuntimeException("Unable to create data files.", exception);
        }
    }

    private void createFileIfMissing(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public void saveRooms(List<Room> rooms) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ROOMS_FILE))) {
            for (Room room : rooms) {
                writer.write(room.getRoomNumber() + "," +
                        room.getRoomType() + "," +
                        room.getPrice() + "," +
                        room.isAvailable());
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException("Unable to save rooms.", exception);
        }
    }

    public List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(ROOMS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] data = line.split(",");
                Room room = new Room(
                        data[0],
                        data[1],
                        Double.parseDouble(data[2]),
                        Boolean.parseBoolean(data[3])
                );
                rooms.add(room);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load rooms.", exception);
        }

        return rooms;
    }

    public void saveCustomers(List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer customer : customers) {
                writer.write(customer.getCustomerId() + "," +
                        customer.getName() + "," +
                        customer.getContact() + "," +
                        safeValue(customer.getAssignedRoomNumber()));
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException("Unable to save customers.", exception);
        }
    }

    public List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] data = line.split(",");
                Customer customer = new Customer(
                        Integer.parseInt(data[0]),
                        data[1],
                        data[2],
                        emptyToNull(data[3])
                );
                customers.add(customer);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load customers.", exception);
        }

        return customers;
    }

    public void saveBookings(List<Booking> bookings) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking booking : bookings) {
                writer.write(booking.getBookingId() + "," +
                        booking.getCustomer().getCustomerId() + "," +
                        booking.getRoom().getRoomNumber() + "," +
                        booking.getCheckInDate() + "," +
                        booking.getCheckOutDate() + "," +
                        booking.getNumberOfDays() + "," +
                        booking.getServiceCharge() + "," +
                        booking.isActive());
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException("Unable to save bookings.", exception);
        }
    }

    public List<Booking> loadBookings(List<Customer> customers, List<Room> rooms) {
        List<Booking> bookings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] data = line.split(",");
                Customer customer = findCustomerById(customers, Integer.parseInt(data[1]));
                Room room = findRoomByNumber(rooms, data[2]);

                if (customer == null || room == null) {
                    continue;
                }

                Booking booking = new Booking(
                        Integer.parseInt(data[0]),
                        customer,
                        room,
                        LocalDate.parse(data[3]),
                        LocalDate.parse(data[4]),
                        Integer.parseInt(data[5]),
                        Double.parseDouble(data[6]),
                        Boolean.parseBoolean(data[7])
                );
                bookings.add(booking);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load bookings.", exception);
        }

        return bookings;
    }

    private Customer findCustomerById(List<Customer> customers, int customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId() == customerId) {
                return customer;
            }
        }
        return null;
    }

    private Room findRoomByNumber(List<Room> rooms, String roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber().equals(roomNumber)) {
                return room;
            }
        }
        return null;
    }

    private String safeValue(String value) {
        return value == null ? "" : value;
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
