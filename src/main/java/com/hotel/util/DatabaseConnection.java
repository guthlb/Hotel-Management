package com.hotel.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DATABASE_URL = "jdbc:sqlite:hotel.db";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to connect to database: hotel.db", exception);
        }
    }

    public static void initializeDatabase() {
        String createRoomTable = """
                CREATE TABLE IF NOT EXISTS Room (
                    roomNumber TEXT PRIMARY KEY,
                    type TEXT,
                    price REAL,
                    availability INTEGER
                )
                """;

        String createCustomerTable = """
                CREATE TABLE IF NOT EXISTS Customer (
                    name TEXT,
                    contact TEXT,
                    roomAssigned TEXT,
                    totalBill REAL DEFAULT 0
                )
                """;

        String createBookingTable = """
                CREATE TABLE IF NOT EXISTS Booking (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    customerName TEXT,
                    roomNumber TEXT,
                    status TEXT,
                    totalBill REAL DEFAULT 0
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createRoomTable);
            statement.execute(createCustomerTable);
            statement.execute(createBookingTable);
            addColumnIfMissing(statement, "ALTER TABLE Customer ADD COLUMN totalBill REAL DEFAULT 0");
            addColumnIfMissing(statement, "ALTER TABLE Booking ADD COLUMN totalBill REAL DEFAULT 0");
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to initialize database tables.", exception);
        }
    }

    private static void addColumnIfMissing(Statement statement, String alterStatement) {
        try {
            statement.execute(alterStatement);
        } catch (SQLException exception) {
            String message = exception.getMessage();
            if (message == null || !message.toLowerCase().contains("duplicate column")) {
                throw new RuntimeException("Unable to update database schema.", exception);
            }
        }
    }
}
