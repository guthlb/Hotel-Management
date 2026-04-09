package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.Room;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    private static final ArrayList<Booking> bookings = new ArrayList<>();
    private static int nextBookingId = 1;

    public boolean createBooking(Customer customer, Room room) {
        if (customer == null || room == null || !room.isAvailable()) {
            return false;
        }

        for (Booking booking : bookings) {
            if (booking.isActive() && booking.getRoom().getRoomNumber().equals(room.getRoomNumber())) {
                return false;
            }
        }

        customer.setAssignedRoomNumber(room.getRoomNumber());
        room.setAvailable(false);

        Booking booking = new Booking(
                nextBookingId,
                customer,
                room,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                1,
                0.0,
                true
        );

        bookings.add(booking);
        nextBookingId++;
        return true;
    }

    public boolean checkoutBooking(Booking booking) {
        if (booking == null || !booking.isActive()) {
            return false;
        }

        booking.setActive(false);
        booking.getRoom().setAvailable(true);
        booking.getCustomer().setAssignedRoomNumber(null);
        return true;
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }
}
