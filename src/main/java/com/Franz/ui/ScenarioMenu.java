package com.Franz.ui;

import com.Franz.dao.ScenarioDao;
import com.Franz.model.ScenarioEntity;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Pharmacy Stock Entry & Inventory Tracking System (console UI).
 * Interactive console menu. It only talks to the user and calls the DAO. There is no SQL in here.
 */
public class ScenarioMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final ScenarioDao dao = new ScenarioDao();
    private static final String[] CATEGORIES = {"ANTIBIOTIC", "PAINKILLER", "VITAMIN"};
    private static final int MAX_BRAND_LENGTH = 20;
    private static final double MAX_PRICE = 1_000_000.00;

    public void run() {
        // Keep "." as the decimal separator for typing prices and for printf output.
        Locale.setDefault(Locale.US);
        scanner.useLocale(Locale.US);

        boolean running = true;
        try {
            while (running) {
                printMenu();
                int choice = readInt("Choose an option: ", 1, 3);

                switch (choice) {
                    case 1 -> addMedicine();
                    case 2 -> viewInventory();
                    case 3 -> {
                        System.out.println("Exiting system. Goodbye!");
                        running = false;
                    }
                }
            }
        } catch (NoSuchElementException e) {
            // The input stream was closed (e.g. Ctrl+D), so there is nothing left to read.
            System.out.println();
            System.out.println("Input closed. Exiting system.");
        }

        scanner.close();
    }

    // ---------------------------------------------------------------- menu

    private void printMenu() {
        System.out.println();
        System.out.println("===== PHARMACY STOCK SYSTEM =====");
        System.out.println("[1] Add New Medicine");
        System.out.println("[2] View Medicine Inventory");
        System.out.println("[3] Exit System");
    }

    // ------------------------------------------------------------ add flow

    private void addMedicine() {
        System.out.println();
        System.out.println("--- Add New Medicine ---");

        // Prompt order from the problem sheet: Brand Name, Category, Price, Stock.
        String brandName = readBrandName();
        String category = readCategory();
        double price = readPrice();
        int stock = readInt("Stock quantity: ", 0, 1_000_000);

        ScenarioEntity medicine = new ScenarioEntity(brandName, category, price, stock);

        if (dao.insert(medicine)) {
            System.out.println("Medicine saved successfully!");
        } else {
            System.out.println("Medicine was NOT saved. Please try again.");
        }
    }

    // ----------------------------------------------------------- view flow

    private void viewInventory() {
        List<ScenarioEntity> medicines = dao.findAll();

        System.out.println();
        System.out.println("--- Medicine Inventory ---");

        if (medicines.isEmpty()) {
            System.out.println("No medicines in inventory yet.");
            return;
        }

        String line = "+------+----------------------+------------+------------+---------+";

        System.out.println(line);
        System.out.printf("| %-4s | %-20s | %-10s | %10s | %7s |%n",
                "ID", "BRAND NAME", "CATEGORY", "PRICE", "STOCK");
        System.out.println(line);

        long totalStock = 0;
        double totalValue = 0;

        for (ScenarioEntity m : medicines) {
            System.out.printf("| %-4d | %-20s | %-10s | %10.2f | %7d |%n",
                    m.id(), m.brandName(), m.category(), m.price(), m.stock());
            totalStock += m.stock();
            totalValue += m.price() * m.stock();
        }

        System.out.println(line);
        System.out.printf("Total medicines : %d%n", medicines.size());
        System.out.printf("Total stock     : %d units%n", totalStock);
        System.out.printf("Inventory value : %,.2f%n", totalValue);
    }

    // ------------------------------------------------------ input helpers

    // Reads a whole number between min and max. Keeps asking until the input is valid.
    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);

            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine(); // flush the leftover newline so the next String read is not skipped

                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number from " + min + " to " + max + ".");
            } else {
                scanner.nextLine(); // throw away the invalid text
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    // Reads a positive price, rounded to 2 decimal places.
    private double readPrice() {
        while (true) {
            System.out.print("Price (e.g. 8.50): ");

            if (scanner.hasNextDouble()) {
                double value = scanner.nextDouble();
                scanner.nextLine(); // flush the leftover newline

                // value <= MAX_PRICE is false for NaN and Infinity too, so they are rejected as well.
                double price = Math.round(value * 100.0) / 100.0;
                if (value <= MAX_PRICE && price > 0) {
                    return price;
                }
                System.out.println("Price must be between 0.01 and 1,000,000.00.");
            } else {
                scanner.nextLine();
                System.out.println("Invalid input. Enter a price like 8.50.");
            }
        }
    }

    // Reads a non-empty brand name that fits the table column.
    private String readBrandName() {
        while (true) {
            System.out.print("Brand name (max " + MAX_BRAND_LENGTH + " characters): ");
            String name = scanner.nextLine().trim();

            if (!name.isEmpty() && name.length() <= MAX_BRAND_LENGTH) {
                return name;
            }
            System.out.println("Brand name must be 1 to " + MAX_BRAND_LENGTH + " characters.");
        }
    }

    // Reads one of the allowed categories (not case-sensitive).
    private String readCategory() {
        while (true) {
            System.out.print("Category (ANTIBIOTIC / PAINKILLER / VITAMIN): ");
            String input = scanner.nextLine().trim().toUpperCase();

            for (String category : CATEGORIES) {
                if (category.equals(input)) {
                    return category;
                }
            }
            System.out.println("Invalid category. Choose ANTIBIOTIC, PAINKILLER, or VITAMIN.");
        }
    }
}
