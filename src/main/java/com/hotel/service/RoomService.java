package com.hotel.service;

import com.hotel.model.Room;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomService {

    private static final String DATA_FOLDER = "data";
    private static final String ROOM_FILE = DATA_FOLDER + "/rooms.txt";

    private final ArrayList<Room> rooms = new ArrayList<>();

    public RoomService() {
        createFileIfNeeded();
        loadFromFile();
    }

    public void addRoom(Room room) {
        rooms.add(room);
        saveToFile();
    }

    public void updateRoomAvailability(String roomNumber, boolean available) {
        for (Room room : rooms) {
            if (room.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                room.setAvailable(available);
                break;
            }
        }
        saveToFile();
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ROOM_FILE))) {
            for (Room room : rooms) {
                writer.write(room.getRoomNumber() + "," +
                        room.getRoomType() + "," +
                        room.getPrice() + "," +
                        room.isAvailable());
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException("Unable to save rooms to file.", exception);
        }
    }

    public void loadFromFile() {
        rooms.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(ROOM_FILE))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] data = line.split(",");
                if (data.length != 4) {
                    continue;
                }

                Room room = new Room(
                        data[0],
                        data[1],
                        Double.parseDouble(data[2]),
                        Boolean.parseBoolean(data[3])
                );
                rooms.add(room);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load rooms from file.", exception);
        }
    }

    private void createFileIfNeeded() {
        try {
            File folder = new File(DATA_FOLDER);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File roomFile = new File(ROOM_FILE);
            if (!roomFile.exists()) {
                roomFile.createNewFile();
            }
        } catch (IOException exception) {
            throw new RuntimeException("Unable to create room file.", exception);
        }
    }
}
