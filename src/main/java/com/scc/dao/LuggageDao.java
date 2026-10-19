package com.scc.dao;

import com.scc.model.Luggage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LuggageDao {

    // Saves one checked-in baggage. The ID is assigned automatically by the database.
    public boolean addLuggage(String passengerName, String flightNo, double weightKg, String status) {
        String sql = "INSERT INTO luggage (passenger_name, flight_no, weight_kg, status) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, passengerName);
            pstmt.setString(2, flightNo);
            pstmt.setDouble(3, weightKg);
            pstmt.setString(4, status);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Check-in failed: " + e.getMessage());
            return false;
        }
    }

    // All checked-in luggage, oldest first
    public List<Luggage> getAllLuggage() {
        List<Luggage> list = new ArrayList<>();
        String sql = "SELECT luggage_id, passenger_name, flight_no, weight_kg, status " +
                "FROM luggage ORDER BY luggage_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Luggage(
                        rs.getInt("luggage_id"),
                        rs.getString("passenger_name"),
                        rs.getString("flight_no"),
                        rs.getDouble("weight_kg"),
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            System.err.println("Could not load luggage: " + e.getMessage());
        }
        return list;
    }
}