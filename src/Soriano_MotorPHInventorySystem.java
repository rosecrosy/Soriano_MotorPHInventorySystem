/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ERDA FOUNDATION
 */
package soriano_motorphinventorysystem;

import java.io.*;
import java.util.*;

public class Soriano_MotorPHInventorySystem {
    static class Stock implements Comparable<Stock> {
        String engineNumber;
        String brand;
        String stockLabel;
        String status;
        Date dateEntered;

        public Stock(String engineNumber, String brand, String stockLabel, String status, Date dateEntered) {
            this.engineNumber = engineNumber;
            this.brand = brand;
            this.stockLabel = stockLabel;
            this.status = status;
            this.dateEntered = dateEntered;
        }

        @Override
        public int compareTo(Stock other) {
            return this.brand.compareTo(other.brand);
        }

        @Override
        public String toString() {
            return "Engine Number: " + engineNumber + ", Brand: " + brand + ", Stock Label: " + stockLabel +
                   ", Status: " + status + ", Date Entered: " + dateEntered;
        }
    }

    private static Map<String, Stock> stockMap = new HashMap<>();
    private static List<Stock> stockList = new ArrayList<>();

    public static void addStock(String engineNumber, String brand, String stockLabel, String status) {
        if (stockMap.containsKey(engineNumber)) {
            System.out.println("Error: Engine number " + engineNumber + " already exists.");
            return;
        }
        Stock newStock = new Stock(engineNumber, brand, stockLabel, status, new Date());
        stockMap.put(engineNumber, newStock);
        stockList.add(newStock);
        System.out.println("Stock added successfully.");
    }

    public static void deleteStock(String engineNumber) {
        if (!stockMap.containsKey(engineNumber)) {
            System.out.println("Error: Engine number not found.");
            return;
        }
        Stock stock = stockMap.get(engineNumber);
        if (stock.stockLabel.equalsIgnoreCase("new") || stock.status.equalsIgnoreCase("on-hand")) {
            System.out.println("Error: Cannot delete stock that is 'New' or 'On-hand'.");
            return;
        }
        stockMap.remove(engineNumber);
        stockList.remove(stock);
        System.out.println("Stock deleted successfully.");
    }

    public static void searchStock(String engineNumber) {
        if (stockMap.containsKey(engineNumber)) {
            System.out.println(stockMap.get(engineNumber));
        } else {
            System.out.println("No records found for engine number " + engineNumber);
        }
    }

    public static void sortStock(String criteria, boolean ascending) {
        if (stockList.isEmpty()) {
            System.out.println("Error: Inventory is empty.");
            return;
        }
        Comparator<Stock> comparator;
        switch (criteria.toLowerCase()) {
            case "brand":
                comparator = Comparator.comparing(s -> s.brand);
                break;
            case "date":
                comparator = Comparator.comparing(s -> s.dateEntered);
                break;
            case "stocklabel":
                comparator = Comparator.comparing(s -> s.stockLabel);
                break;
            case "status":
                comparator = Comparator.comparing(s -> s.status);
                break;
            case "enginenumber":
                comparator = Comparator.comparing(s -> s.engineNumber);
                break;
            default:
                System.out.println("Invalid sorting criteria.");
                return;
        }
        if (!ascending) {
            comparator = comparator.reversed();
        }
        stockList.sort(comparator);
        stockList.forEach(System.out::println);
    }

    public static void loadFromCSV() {
        String filePath = "src/inventory.csv";
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Warning: CSV file not found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length == 5) {
                    String dateEntered = values[0];
                    String stockLabel = values[1];
                    String brand = values[2];
                    String engineNumber = values[3];
                    String status = values[4];
                    if (!stockMap.containsKey(engineNumber)) {
                        Stock newStock = new Stock(engineNumber, brand, stockLabel, status, new Date());
                        stockMap.put(engineNumber, newStock);
                        stockList.add(newStock);
                    }
                }
            }
            System.out.println("CSV file loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        loadFromCSV(); // Load CSV data at the start

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMotorPH Inventory System");
            System.out.println("1. Add Stock");
            System.out.println("2. Delete Stock");
            System.out.println("3. Search Stock");
            System.out.println("4. Sort Inventory");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter Engine Number: ");
                    String engineNumber = scanner.nextLine();
                    System.out.print("Enter Brand: ");
                    String brand = scanner.nextLine();
                    System.out.print("Enter Stock Label (new/old): ");
                    String stockLabel = scanner.nextLine();
                    System.out.print("Enter Status (on-hand/sold): ");
                    String status = scanner.nextLine();
                    addStock(engineNumber, brand, stockLabel, status);
                    break;
                case 2:
                    System.out.print("Enter Engine Number to Delete: ");
                    String deleteEngineNumber = scanner.nextLine();
                    deleteStock(deleteEngineNumber);
                    break;
                case 3:
                    System.out.print("Enter Engine Number to Search: ");
                    String searchEngineNumber = scanner.nextLine();
                    searchStock(searchEngineNumber);
                    break;
                case 4:
                    System.out.println("Sort by: brand, date, stockLabel, status, engineNumber");
                    String criteria = scanner.nextLine();
                    System.out.print("Order (asc/desc): ");
                    boolean ascending = scanner.nextLine().equalsIgnoreCase("asc");
                    sortStock(criteria, ascending);
                    break;
                case 5:
                    System.out.println("Exiting system.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
