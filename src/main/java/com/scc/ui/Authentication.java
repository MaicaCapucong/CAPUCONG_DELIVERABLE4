package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class Authentication {
    private final UserDao userDao;
    private final Scanner sc;

    public Authentication(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
        this.sc = sc;
    }

    public void start() {
        System.out.println("========================================");
        System.out.println("  BARANGAY CLEARANCE REQUEST SYSTEM    ");
        System.out.println("========================================");

        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1" -> loginFlow();
                case "2" -> registerFlow();
                case "3" -> { System.out.println("👋 Goodbye!"); return; }
                default -> System.out.println("❌ Choose 1-3");
            }
        }
    }

    private void loginFlow() {
        System.out.print("Username: ");
        String user = sc.nextLine().trim();
        System.out.print("Password: ");
        String pass = sc.nextLine().trim();

        User loggedIn = userDao.login(user, pass);
        if (loggedIn == null) return;

        System.out.println("\n✅ Welcome, " + loggedIn.username() + " [" + loggedIn.roles() + "]");
        routeToMenu(loggedIn);
    }

    private void registerFlow() {
        System.out.print("New Username: ");
        String user = sc.nextLine().trim();
        System.out.print("New Password: ");
        String pass = sc.nextLine().trim();

        System.out.println("Role: 1=SECRETARY | 2=BARANGAY OFFICIAL | 3=RESIDENT");
        System.out.print("Choice: ");
        try {
            int opt = Integer.parseInt(sc.nextLine().trim());
            String role = switch (opt) {
                case 1 -> "SECRETARY";
                case 2 -> "BARANGAY OFFICIAL";
                case 3 -> "RESIDENT";
                default -> null;
            };
            if (role == null) { System.out.println("❌ Invalid role"); return; }
            userDao.registerUser(user, pass, role);
        } catch (NumberFormatException e) {
            System.out.println("❌ Enter a number");
        }
    }

    private void routeToMenu(User user) {
        switch (user.roles()) {
            case "SECRETARY" -> new AdminMenu(userDao, sc).display();
            case "BARANGAY OFFICIAL" -> System.out.println("📋 Official Dashboard");
            case "RESIDENT" -> System.out.println("🏠 Resident Dashboard");
            default -> System.out.println("⚠️ Unknown role");
        }
    }
}