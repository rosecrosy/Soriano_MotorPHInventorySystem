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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Soriano_MotorPHInventorySystem {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");

    // Define the Stock class
    static class Stock {
        String dateEntered;
        String stockLabel;
        String brand;
        String engineNumber;
        String status;

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

    // Binary Search Tree (BST) Node
    static class BSTNode {
        Stock stock;
        BSTNode left, right;

        public BSTNode(Stock stock) {
            this.stock = stock;
            this.left = this.right = null;
        }
    }

    // Binary Search Tree (BST) for inventory
    static class InventoryBST {
        private BSTNode root;
        private HashMap<String, Stock> stockMap = new HashMap<>(); // Hashing for fast lookups

        // Insert stock recursively
        public void insert(Stock stock) {
            root = insertRec(root, stock);
            stockMap.put(stock.engineNumber, stock); // Store in HashMap for quick lookup
        }

        private BSTNode insertRec(BSTNode root, Stock stock) {
            if (root == null) return new BSTNode(stock);
            if (stock.engineNumber.compareTo(root.stock.engineNumber) < 0)
                root.left = insertRec(root.left, stock);
            else
                root.right = insertRec(root.right, stock);
            return root;
        }

        // Search stock recursively
        public Stock search(String engineNumber) {
            return searchRec(root, engineNumber);
        }

        private Stock searchRec(BSTNode root, String engineNumber) {
            if (root == null) return null;
            if (root.stock.engineNumber.equals(engineNumber)) return root.stock;
            return engineNumber.compareTo(root.stock.engineNumber) < 0 ?
                    searchRec(root.left, engineNumber) :
                    searchRec(root.right, engineNumber);
        }

        // Delete stock recursively
        public void delete(String engineNumber) {
            root = deleteRec(root, engineNumber);
            stockMap.remove(engineNumber); // Remove from HashMap
        }

        private BSTNode deleteRec(BSTNode root, String engineNumber) {
            if (root == null) return root;
            if (engineNumber.compareTo(root.stock.engineNumber) < 0)
                root.left = deleteRec(root.left, engineNumber);
            else if (engineNumber.compareTo(root.stock.engineNumber) > 0)
                root.right = deleteRec(root.right, engineNumber);
            else {
                if (root.left == null) return root.right;
                if (root.right == null) return root.left;
                root.stock = findMin(root.right).stock;
                root.right = deleteRec(root.right, root.stock.engineNumber);
            }
            return root;
        }

        private BSTNode findMin(BSTNode root) {
            while (root.left != null) root = root.left;
            return root;
        }

        // Convert BST to List for sorting
        public List<Stock> toList() {
            List<Stock> stockList = new ArrayList<>();
            inorderTraversal(root, stockList);
            return stockList;
        }

        private void inorderTraversal(BSTNode root, List<Stock> stockList) {
            if (root != null) {
                inorderTraversal(root.left, stockList);
                stockList.add(root.stock);
                inorderTraversal(root.right, stockList);
            }
        }
    }

    // Merge Sort Algorithm for sorting
    public static void mergeSort(List<Stock> stockList, Comparator<Stock> comparator) {
        if (stockList.size() < 2) return;
        int mid = stockList.size() / 2;
        List<Stock> left = new ArrayList<>(stockList.subList(0, mid));
        List<Stock> right = new ArrayList<>(stockList.subList(mid, stockList.size()));
        mergeSort(left, comparator);
        mergeSort(right, comparator);
        merge(stockList, left, right, comparator);
    }

    private static void merge(List<Stock> stockList, List<Stock> left, List<Stock> right, Comparator<Stock> comparator) {
        int i = 0, j = 0, k = 0;
        while (i < left.size() && j < right.size()) {
            if (comparator.compare(left.get(i), right.get(j)) <= 0)
                stockList.set(k++, left.get(i++));
            else
                stockList.set(k++, right.get(j++));
        }
        while (i < left.size()) stockList.set(k++, left.get(i++));
        while (j < right.size()) stockList.set(k++, right.get(j++));
    }

    // Load from CSV
    public static void loadFromCSV(InventoryBST inventory) {
        String filePath = "src/inventory.csv";
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Warning: CSV file not found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 5) {
                    inventory.insert(new Stock(values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(), values[4].trim()));
                }
            }
            System.out.println("CSV file loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        InventoryBST inventory = new InventoryBST();
        loadFromCSV(inventory);
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
                    System.out.print("Enter Date Entered: ");
                    String date = scanner.nextLine();
                    System.out.print("Enter Stock Label: ");
                    String stockLabel = scanner.nextLine();
                    System.out.print("Enter Brand: ");
                    String brand = scanner.nextLine();
                    System.out.print("Enter Engine Number: ");
                    String engineNumber = scanner.nextLine();
                    System.out.print("Enter Status: ");
                    String status = scanner.nextLine();
                    inventory.insert(new Stock(date, stockLabel, brand, engineNumber, status));
                    break;
                case 2:
                    System.out.print("Enter Engine Number to Delete: ");
                    inventory.delete(scanner.nextLine());
                    break;
                case 3:
                    System.out.print("Enter Engine Number to Search: ");
                    System.out.println(inventory.search(scanner.nextLine()));
                    break;
                case 4:
                    List<Stock> stockList = inventory.toList();
                    mergeSort(stockList, Comparator.comparing(s -> s.brand));
                    stockList.forEach(System.out::println);
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

