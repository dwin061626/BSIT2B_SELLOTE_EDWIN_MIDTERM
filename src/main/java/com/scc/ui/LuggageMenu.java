package com.scc.ui;

import com.scc.dao.LuggageDao;
import com.scc.model.Luggage;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class LuggageMenu {

    private static final int MENU_WIDTH = 60;
    private static final int TABLE_WIDTH = 66;
    private static final String TABLE_BORDER =
            "+------+----------------------+-----------+-----------+----------+";

    private final Scanner sc;
    private final LuggageDao luggageDao = new LuggageDao();

    // Share the Scanner from Main so two Scanners don't fight over System.in
    public LuggageMenu(Scanner sc) {
        this.sc = sc;
    }

    //////////////////////////////////////////////////////////////
    // MAIN MENU LOOP
    //////////////////////////////////////////////////////////////

    public void showMenu() {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readChoice();

            switch (choice) {
                case 1:
                    checkInLuggage();
                    break;
                case 2:
                    viewCheckedLuggage();
                    break;
                case 3:
                    System.out.println("\nExiting system... Thank you!");
                    running = false;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please enter 1, 2 or 3.");
                    break;
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=".repeat(MENU_WIDTH));
        System.out.println(center("AIRPORT LUGGAGE CLAIM REGISTRY", MENU_WIDTH));
        System.out.println("=".repeat(MENU_WIDTH));
        System.out.println("[1] Check-In Luggage");
        System.out.println("[2] View Checked Luggage");
        System.out.println("[3] Exit System");
        System.out.println("=".repeat(MENU_WIDTH));
        System.out.print("Enter choice: ");
    }

    //////////////////////////////////////////////////////////////
    // CHECK-IN LUGGAGE
    //////////////////////////////////////////////////////////////

    private void checkInLuggage() {
        System.out.println("\n--- CHECK-IN LUGGAGE ---");

        String name = readRequired("Enter Passenger Name: ");
        String flight = readRequired("Enter Flight Number: ").toUpperCase();
        double weight = readWeight();
        String status = readStatus();

        if (luggageDao.addLuggage(name, flight, weight, status)) {
            System.out.println("\nSUCCESS: Baggage successfully checked in!");
        } else {
            System.out.println("\nFAILED: Baggage could not be saved.");
        }
    }

    //////////////////////////////////////////////////////////////
    // VIEW CHECKED LUGGAGE
    //////////////////////////////////////////////////////////////

    private void viewCheckedLuggage() {
        System.out.println();
        System.out.println("=".repeat(TABLE_WIDTH));
        System.out.println(center("CHECKED LUGGAGE LOGS", TABLE_WIDTH));
        System.out.println("=".repeat(TABLE_WIDTH));

        List<Luggage> records = luggageDao.getAllLuggage();
        if (records.isEmpty()) {
            System.out.println("\nNo luggage records found.");
            return;
        }

        System.out.println(TABLE_BORDER);
        System.out.printf("| %-4s | %-20s | %-9s | %-9s | %-8s |%n",
                "ID", "PASSENGER NAME", "FLIGHT NO", "WEIGHT_KG", "STATUS");
        System.out.println(TABLE_BORDER);

        for (Luggage l : records) {
            // Locale.US keeps the decimal point as "." on every computer
            System.out.printf(Locale.US, "| %-4d | %-20s | %-9s | %-9.1f | %-8s |%n",
                    l.id(), fit(l.passengerName(), 20), fit(l.flightNo(), 9),
                    l.weightKg(), l.status());
        }

        System.out.println(TABLE_BORDER);
        System.out.println("Total Records Displayed: " + records.size());
    }

    //////////////////////////////////////////////////////////////
    // INPUT HELPERS
    //////////////////////////////////////////////////////////////
    // Every input is read with nextLine() and converted afterwards, so no newline
    // is ever left in the buffer between a number and the text prompts that follow.

    // Menu choice: returns -1 if the user typed something that is not a number
    private int readChoice() {
        String line = sc.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Text that cannot be blank
    private String readRequired(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty. Please try again.");
        }
    }

    // Weight in kilograms: must be a positive number
    private double readWeight() {
        while (true) {
            System.out.print("Enter Luggage Weight (KG): ");
            String line = sc.nextLine().trim();
            try {
                double weight = Double.parseDouble(line);
                if (Double.isFinite(weight) && weight > 0) {
                    return weight;
                }
            } catch (NumberFormatException e) {
                // fall through to the error message
            }
            System.out.println("Invalid weight. Enter a positive number (e.g. 23.0).");
        }
    }

    // Status: CHECKED, LOADED or CLAIMED (any capitalization)
    private String readStatus() {
        while (true) {
            System.out.print("Enter Status (CHECKED / LOADED / CLAIMED): ");
            String status = sc.nextLine().trim().toUpperCase();
            switch (status) {
                case "CHECKED":
                case "LOADED":
                case "CLAIMED":
                    return status;
                default:
                    System.out.println("Invalid status. Type CHECKED, LOADED or CLAIMED.");
                    break;
            }
        }
    }

    //////////////////////////////////////////////////////////////
    // FORMAT HELPERS
    //////////////////////////////////////////////////////////////

    private String center(String text, int width) {
        int pad = Math.max(0, (width - text.length()) / 2);
        return " ".repeat(pad) + text;
    }

    // Shortens text so the table columns stay aligned
    private String fit(String text, int max) {
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max - 3) + "...";
    }
}