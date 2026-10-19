package com.scc;

import com.scc.dao.DatabaseConnection;
import com.scc.ui.LuggageMenu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Shows which database file is used and creates the luggage table if it is missing
        DatabaseConnection.initialize();

        Scanner sc = new Scanner(System.in);
        new LuggageMenu(sc).showMenu();
    }
}