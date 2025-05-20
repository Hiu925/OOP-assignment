package IO_Interface;

import java.util.List;
import java.util.Scanner;

public class IOInterface {
    private static IOInterface instance;
    private Scanner scanner;
    
   
    private IOInterface() {
        scanner = new Scanner(System.in);
    }
    
    
    public static IOInterface getInstance() {
        if (instance == null) {
            instance = new IOInterface();
        }
        return instance;
    }
    
    
    public String[] getUserInput(String message, int numOfArgs) {
        System.out.print(message + " ");
        String input = scanner.nextLine().trim();
        String[] args = input.split("\\s+");
        String[] result = new String[numOfArgs];
        
        // Copy input arguments to result array
        for (int i = 0; i < numOfArgs; i++) {
            if (i < args.length) {
                result[i] = args[i];
            } else {
                result[i] = "";  // Fill with empty strings if fewer arguments
            }
        }
        
        return result;
    }
    
    public void mainMenu() {
        System.out.println("\n=== E-Commerce System ===");
        System.out.println("1. Login");
        System.out.println("2. Register (Customer)");
        System.out.println("3. Quit");
        System.out.println("=========================");
        System.out.print("Please select an option: ");
    }
    
    public void adminMenu() {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("1. Show products");
        System.out.println("2. Add customers");
        System.out.println("3. Show customers");
        System.out.println("4. Show orders");
        System.out.println("5. Generate test data");
        System.out.println("6. Generate all statistical figures");
        System.out.println("7. Delete all data");
        System.out.println("8. Logout");
        System.out.println("=================");
        System.out.print("Please select an option: ");
    }
    
    public void customerMenu() {
        System.out.println("\n=== Customer Menu ===");
        System.out.println("1. Show profile");
        System.out.println("2. Update profile");
        System.out.println("3. Show products (optional: add keyword after 3)");
        System.out.println("4. Show history orders");
        System.out.println("5. Generate all consumption figures");
        System.out.println("6. Logout");
        System.out.println("====================");
        System.out.print("Please select an option: ");
    }
    
    
    public void showList(String userRole, String listType, List<?> objectList, 
                         int pageNumber, int totalPages) {
        System.out.println("\n=== " + listType + " List ===");
        System.out.println("Page " + pageNumber + " of " + totalPages);
        System.out.println("------------------------------------------");
        
        if (objectList == null || objectList.isEmpty()) {
            System.out.println("No items found.");
        } else {
            int rowNum = (pageNumber - 1) * 10 + 1;
            for (Object obj : objectList) {
                System.out.println("Row " + rowNum + ": " + obj);
                rowNum++;
            }
        }
        
        System.out.println("------------------------------------------");
        if (totalPages > 1) {
            System.out.println("Navigation: Enter 1 for previous page, 2 for next page");
            if (userRole.equals("admin")) {
                System.out.println("Admin actions: Enter 'delete [id]' to delete an item");
            }
            if (listType.equals("Product") && userRole.equals("customer")) {
                System.out.println("Actions: Enter 'buy [product_id]' to purchase a product");
            }
        }
    }
    
    public void printErrorMessage(String errorSource, String errorMessage) {
        System.out.println("\n[ERROR] " + errorSource + ": " + errorMessage);
    }
    
    
    public void printMessage(String message) {
        System.out.println(message);
    }
    
    
    public void printObject(Object targetObject) {
        if (targetObject != null) {
            System.out.println(targetObject.toString());
        } else {
            System.out.println("Object is null");
        }
    }
    
    
    public void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}