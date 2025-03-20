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

        /** Load inventory from CSV file */
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

        /** Save inventory to CSV file */
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

        /** Add stock with manual date input if engine number is new */
        public boolean addStock(Scanner scanner) {
            System.out.print("Enter Engine Number: ");
            String engineNumber = scanner.nextLine();

            // Check if engine number already exists
            if (stockMap.containsKey(engineNumber)) {
                System.out.println("Error: Engine number already exists.");
                return false;
            }

            // Engine number does not exist, allow user to enter date
            System.out.print("Enter Date (YYYY-MM-DD): ");
            String dateEntered = scanner.nextLine();

            System.out.print("Enter Brand: ");
            String brand = scanner.nextLine();

            System.out.print("Enter Status (on-hand, new, sold, old): ");
            String status = scanner.nextLine();

            Stock newStock = new Stock(dateEntered, "Stock", brand, engineNumber, status);
            stockMap.put(engineNumber, newStock);
            saveToCSV();
            System.out.println("Stock added successfully.");
            return true;
        }

        /** Delete stock from inventory */
        public boolean deleteStock(String engineNumber) {
            if (!stockMap.containsKey(engineNumber)) {
                System.out.println("❌ Error: Stock not found.");
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

        /** Search for a stock */
        public Stock searchStock(String engineNumber) {
            return stockMap.get(engineNumber);
        }

        /** Merge Sort Implementation */
        public void mergeSort(List<Stock> stockList, int left, int right, String criteria) {
            if (left < right) {
                int mid = (left + right) / 2;
                mergeSort(stockList, left, mid, criteria);
                mergeSort(stockList, mid + 1, right, criteria);
                merge(stockList, left, mid, right, criteria);
            }
        }

        private void merge(List<Stock> stockList, int left, int mid, int right, String criteria) {
            List<Stock> leftList = new ArrayList<>(stockList.subList(left, mid + 1));
            List<Stock> rightList = new ArrayList<>(stockList.subList(mid + 1, right + 1));

            int i = 0, j = 0, k = left;

            while (i < leftList.size() && j < rightList.size()) {
                if (compareStocks(leftList.get(i), rightList.get(j), criteria) <= 0) {
                    stockList.set(k++, leftList.get(i++));
                } else {
                    stockList.set(k++, rightList.get(j++));
                }
            }

            while (i < leftList.size()) stockList.set(k++, leftList.get(i++));
            while (j < rightList.size()) stockList.set(k++, rightList.get(j++));
        }

        private int compareStocks(Stock s1, Stock s2, String criteria) {
            switch (criteria.toLowerCase()) {
                case "date": return s1.dateEntered.compareTo(s2.dateEntered);
                case "status": return s1.status.compareTo(s2.status);
                case "brand": return s1.brand.compareTo(s2.brand);
                default: return s1.engineNumber.compareTo(s2.engineNumber);
            }
        }

        /** Sort inventory using Merge Sort */
        public List<Stock> sortStock(String criteria) {
            List<Stock> stockList = new ArrayList<>(stockMap.values());
            mergeSort(stockList, 0, stockList.size() - 1, criteria);
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
                    inventory.addStock(scanner);
                    break;
                case 2:
                    System.out.print("Enter Engine Number to Delete: ");
                    inventory.deleteStock(scanner.nextLine());
                    break;
                case 3:
                    System.out.print("Enter Engine Number to Search: ");
                    Stock foundStock = inventory.searchStock(scanner.nextLine());
                    System.out.println(foundStock != null ? "Found: " + foundStock : "Stock not found.");
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