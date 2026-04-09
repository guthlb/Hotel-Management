package com.hotel.service;

import com.hotel.model.Room;
import com.hotel.util.DatabaseConnection;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomService {

    public void addRoom(Room room) {
        String insertRoom = "INSERT INTO Room (roomNumber, type, price, availability) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertRoom)) {
            statement.setString(1, room.getRoomNumber());
            statement.setString(2, room.getRoomType());
            statement.setDouble(3, room.getPrice());
            statement.setInt(4, room.isAvailable() ? 1 : 0);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to add room to database.", exception);
        }
    }

    public void updateRoomAvailability(String roomNumber, boolean available) {
        String updateRoom = "UPDATE Room SET availability = ? WHERE roomNumber = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateRoom)) {
            statement.setInt(1, available ? 1 : 0);
            statement.setString(2, roomNumber);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to update room availability.", exception);
        }
    }

    public List<Room> getAllRooms() {
        ArrayList<Room> rooms = new ArrayList<>();
        String selectRooms = "SELECT roomNumber, type, price, availability FROM Room";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectRooms);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Room room = new Room(
                        resultSet.getString("roomNumber"),
                        resultSet.getString("type"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("availability") == 1
                );
                rooms.add(room);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to load rooms from database.", exception);
        }

        return rooms;
    }
}
