package com.scc.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_FILE = "AirportLuggageDatabase.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Call once at startup: shows which file is used and creates the table if it is missing
    public static void initialize() {
        System.out.println("Using database: " + new File(DB_FILE).getAbsolutePath());

        String sql = "CREATE TABLE IF NOT EXISTS luggage (" +
                "luggage_id INTEGER PRIMARY KEY AUTOINCREMENT, " +   // IDs start from 1 automatically
                "passenger_name TEXT NOT NULL, " +
                "flight_no TEXT NOT NULL, " +
                "weight_kg REAL NOT NULL, " +
                "status TEXT NOT NULL CHECK (status IN ('CHECKED', 'LOADED', 'CLAIMED')))";

        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.err.println("Could not initialize database: " + e.getMessage());
        }
    }
}