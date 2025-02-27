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
import java.text.SimpleDateFormat;
import java.util.*;

public class Soriano_MotorPHInventorySystem {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String CSV_FILE = "C:\\Users\\ERDA FOUNDATION\\Desktop\\MMDC DSA\\Soriano_MotorPHInventorySystem\\src\\inventory.csv";

    static class Stock {
        String dateEntered, stockLabel, brand, engineNumber, status;

        public Stock(String dateEntered, String stockLabel, String brand, String engineNumber, String status) {
            this.dateEntered = dateEntered;
            this.stockLabel = stockLabel;
            this.brand = brand;
            this.engineNumber = engineNumber;
            this.status = status;
        }

        @Override
        public String toString() {
            return String.format("%s, %s, %s, %s, %s", dateEntered, stockLabel, brand, engineNumber, status);
        }
    }

    static class Inventory {
        private TreeMap<String, Stock> stockMap = new TreeMap<>(); // BST for optimized search

      public void loadFromCSV() {
    try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length == 5) {
                stockMap.put(data[3], new Stock(data[0], data[1], data[2], data[3], data[4]));
            }
        }
        System.out.println("Inventory loaded successfully.");
    } catch (IOException e) {
        System.out.println("File not found: " + CSV_FILE);
        System.out.println("Starting fresh.");
    }
}

        public void saveToCSV() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE))) {
                for (Stock stock : stockMap.values()) {
                    bw.write(stock.toString());
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error saving inventory: " + e.getMessage());
            }
        }

        public boolean addStock(Stock stock) {
            if (stockMap.containsKey(stock.engineNumber)) {
                System.out.println("Error: Engine number already exists.");
                return false;
            }
            stockMap.put(stock.engineNumber, stock);
            saveToCSV();
            System.out.println("Stock added successfully.");
            return true;
        }

        public boolean deleteStock(String engineNumber) {
            if (!stockMap.containsKey(engineNumber)) {
                System.out.println("Error: Stock not found.");
                return false;
            }
            Stock stock = stockMap.get(engineNumber);
            if (!stock.status.equals("sold") && !stock.status.equals("old")) {
                System.out.println("Error: Only 'sold' or 'old' stocks can be deleted.");
                return false;
            }
            stockMap.remove(engineNumber);
            saveToCSV();
            System.out.println("Stock deleted successfully.");
            return true;
        }

        public Stock searchStock(String engineNumber) {
            return stockMap.get(engineNumber);
        }

        public List<Stock> sortStock(String criteria) {
            List<Stock> stockList = new ArrayList<>(stockMap.values());
            stockList.sort(Comparator.comparing(s -> {
                switch (criteria) {
                    case "date": return s.dateEntered;
                    case "status": return s.status;
                    case "brand": return s.brand;
                    default: return s.engineNumber;
                }
            }));
            return stockList;
        }
    }

    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        inventory.loadFromCSV();
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
                    System.out.print("Enter Status (on-hand, new, sold, old): ");
                    String status = scanner.nextLine();
                    String date = dateFormat.format(new Date());
                    inventory.addStock(new Stock(date, "Stock", brand, engineNumber, status));
                    break;
                case 2:
                    System.out.print("Enter Engine Number to Delete: ");
                    inventory.deleteStock(scanner.nextLine());
                    break;
                case 3:
                    System.out.print("Enter Engine Number to Search: ");
                    Stock foundStock = inventory.searchStock(scanner.nextLine());
                    System.out.println(foundStock != null ? foundStock : "Stock not found.");
                    break;
                case 4:
                    System.out.print("Sort by (brand, date, status, engine): ");
                    String criteria = scanner.nextLine();
                    List<Stock> sortedStock = inventory.sortStock(criteria);
                    sortedStock.forEach(System.out::println);
                    break;
                case 5:
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
