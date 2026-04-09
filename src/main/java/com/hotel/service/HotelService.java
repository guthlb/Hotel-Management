package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.Room;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HotelService {

    private final List<Room> rooms;
    private final List<Customer> customers;
    private final List<Booking> bookings;
    private final FileStorageService fileStorageService;

    public HotelService() {
        fileStorageService = new FileStorageService();
        rooms = new ArrayList<>(fileStorageService.loadRooms());
        customers = new ArrayList<>(fileStorageService.loadCustomers());
        bookings = new ArrayList<>(fileStorageService.loadBookings(customers, rooms));
    }

    public void addRoom(Room room) {
        rooms.add(room);
        fileStorageService.saveRooms(rooms);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        fileStorageService.saveCustomers(customers);
    }

    public boolean createBooking(int bookingId, int customerId, String roomNumber,
                                 LocalDate checkInDate, LocalDate checkOutDate,
                                 int numberOfDays, double serviceCharge) {
        Room room = findRoomByNumber(roomNumber);
        Customer customer = findCustomerById(customerId);

        if (room == null || customer == null || !room.isAvailable()) {
            return false;
        }

        Booking booking = new Booking(
                bookingId,
                customer,
                room,
                checkInDate,
                checkOutDate,
                numberOfDays,
                serviceCharge,
                true
        );

        room.setAvailable(false);
        customer.setAssignedRoomNumber(roomNumber);
        bookings.add(booking);
        saveAllData();
        return true;
    }

    public boolean checkoutCustomer(int bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId && booking.isActive()) {
                booking.setActive(false);
                booking.getRoom().setAvailable(true);
                booking.getCustomer().setAssignedRoomNumber(null);
                saveAllData();
                return true;
            }
        }

        return false;
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();

        for (Room room : rooms) {
            if (room.isAvailable()) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

    public Room findRoomByNumber(String roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                return room;
            }
        }
        return null;
    }

    public Customer findCustomerById(int customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId() == customerId) {
                return customer;
            }
        }
        return null;
    }

    public Booking findBookingById(int bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                return booking;
            }
        }
        return null;
    }

    public boolean deleteRoom(String roomNumber) {
        Iterator<Room> iterator = rooms.iterator();

        while (iterator.hasNext()) {
            Room room = iterator.next();
            if (room.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                iterator.remove();
                fileStorageService.saveRooms(rooms);
                return true;
            }
        }

        return false;
    }

    private void saveAllData() {
        fileStorageService.saveRooms(rooms);
        fileStorageService.saveCustomers(customers);
        fileStorageService.saveBookings(bookings);
    }
}
