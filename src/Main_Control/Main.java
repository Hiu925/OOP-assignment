package Main_Control;

import java.io.*;
import Model.*;
import Operation.*;
import Helper_Classes.*;
import IO_Interface.*;
import java.util.*;

public class Main {
    
    // Singleton instances
    private static IOInterface ioInterface = IOInterface.getInstance();
    private static UserOperation userOp = UserOperation.getInstance();
    private static CustomerOperation customerOp = CustomerOperation.getInstance();
    private static AdminOperation adminOp = AdminOperation.getInstance();
    private static ProductOperation productOp = ProductOperation.getInstance();
    private static OrderOperation orderOp = OrderOperation.getInstance();
    
    // Current logged in user
    private static User currentUser = null;
    
    public static void main(String[] args) {
        try {
            // Initialize system
            initializeSystem();
            
            // Main application loop
            runApplication();
            
        } catch (Exception e) {
            ioInterface.printErrorMessage("Main", "System error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    //Initialize the system - create necessary directories and files
    private static void initializeSystem() {
        try {
            // Create data directory if not exists
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                ioInterface.printMessage("Created data directory.");
            }
            
            // Create figure directory if not exists
            File figureDir = new File("data/figure");
            if (!figureDir.exists()) {
                figureDir.mkdirs();
                ioInterface.printMessage("Created figure directory.");
            }
            
            // Create necessary files if they don't exist
            createFileIfNotExists("data/users.txt");
            createFileIfNotExists("data/products.txt");
            createFileIfNotExists("data/orders.txt");
            
            // Register admin account
            adminOp.registerAdmin();
            
            // Extract products from files (if needed)
            productOp.extractProductsFromFiles();
            
            ioInterface.printMessage("System initialized successfully!");
            
        } catch (Exception e) {
            ioInterface.printErrorMessage("Main", "Failed to initialize system: " + e.getMessage());
        }
    }
    
    //Checkfile and create
    private static void createFileIfNotExists(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
                ioInterface.printMessage("Created file: " + filepath);
            }
        } catch (Exception e) {
            ioInterface.printErrorMessage("Main", "Failed to create file " + filepath + ": " + e.getMessage());
        }
    }
    
    //Main app loop
    private static void runApplication() {
        boolean running = true;
        
        while (running) {
            try {
                if (currentUser == null) {
                    // User not logged in - show main menu
                    running = handleMainMenu();
                } else {
                    // User logged in - show appropriate menu
                    if (currentUser instanceof Admin) { //kiểm tra kiểu
                        running = handleAdminMenu();
                    } else if (currentUser instanceof Customer) {
                        running = handleCustomerMenu();
                    }
                }
            } catch (Exception e) {
                ioInterface.printErrorMessage("Main", "Error in main loop: " + e.getMessage());
                currentUser = null; // Logout on error
            }
        }
        
        ioInterface.printMessage("Thank you for using the E-Commerce System!");
    }
    
    //Main menu
    private static boolean handleMainMenu() {
        try {
            ioInterface.mainMenu();
            String[] input = ioInterface.getUserInput("Choose an option (1-3): ", 1);
            
            if (input.length == 0 || input[0].isEmpty()) {
                ioInterface.printErrorMessage("Main", "Please enter a valid option.");
                return true;
            }
            
            switch (input[0]) {
                case "1":
                    // Login
                    handleLogin();
                    break;
                case "2":
                    // Register
                    handleRegister();
                    break;
                case "3":
                    // Quit
                    return false;
                default:
                    ioInterface.printErrorMessage("Main", "Invalid option. Please choose 1, 2, or 3.");
            }
            
            return true;
            
        } catch (Exception e) {
            ioInterface.printErrorMessage("Main", "Error in main menu: " + e.getMessage());
            return true;
        }
    }
    
    // Login 

    private static void handleLogin() {
        try {
            String[] input = ioInterface.getUserInput("Enter username and password (separated by space): ", 2);
            
            if (input.length < 2 || input[0].isEmpty() || input[1].isEmpty()) {
                ioInterface.printErrorMessage("Login", "Please provide both username and password.");
                return;
            }
            
            User user = userOp.login(input[0], input[1]);
            // System.out.println(input[0] + " " + input[1]); check
            if (user != null) {
                currentUser = user;
                ioInterface.printMessage("Login successful! Welcome " + user.getUserName());
            } else {
                ioInterface.printErrorMessage("Login", "Invalid username or password.");
            }
            
        } catch (Exception e) {
            ioInterface.printErrorMessage("Login", "Login error: " + e.getMessage());
        }
    }
    
    //Register account
    private static void handleRegister() {
        try {
            ioInterface.printMessage("=== Customer Registration ===");
            String[] input = ioInterface.getUserInput("Enter username, password, email, mobile (separated by spaces): ", 4);
            
            if (input.length < 4 || input[0].isEmpty() || input[1].isEmpty() || 
                input[2].isEmpty() || input[3].isEmpty()) {
                ioInterface.printErrorMessage("Register", "Please provide all required information.");
                return;
            }
            
            boolean success = customerOp.registerCustomer(input[0], input[1], input[2], input[3]);
            
            if (success) {
                ioInterface.printMessage("Registration successful! You can now login.");
            } else {
                ioInterface.printErrorMessage("Register", "Registration failed. Please check your input.");
            }
            
        } catch (Exception e) {
            ioInterface.printErrorMessage("Register", "Registration error: " + e.getMessage());
        }
    }
    
    //Admin menu
    private static boolean handleAdminMenu() {
        try {
            ioInterface.adminMenu();
            String[] input = ioInterface.getUserInput("Choose an option (1-8): ", 1);
            
            if (input.length == 0 || input[0].isEmpty()) {
                ioInterface.printErrorMessage("Admin", "Please enter a valid option.");
                return true;
            }
            
            switch (input[0]) {
                case "1":
                    // Show products
                    handleShowProducts("admin", "");
                    break;
                case "2":
                    // Add customers (redirect to register)
                    handleRegister();
                    break;
                case "3":
                    // Show customers
                    handleShowCustomers();
                    break;
                case "4":
                    // Show orders
                    handleShowOrders("admin");
                    break;
                case "5":
                    // Generate test data
                    handleGenerateTestData();
                    break;
                case "6":
                    // Generate all statistical figures
                    handleGenerateStatisticalFigures();
                    break;
                case "7":
                    // Delete all data
                    handleDeleteAllData();
                    break;
                case "8":
                    // Logout
                    currentUser = null;
                    ioInterface.printMessage("Logged out successfully.");
                    break;
                default:
                    ioInterface.printErrorMessage("Admin", "Invalid option. Please choose 1-8.");
            }
            
            return true;
            
        } catch (Exception e) {
            ioInterface.printErrorMessage("Admin", "Admin menu error: " + e.getMessage());
            return true;
        }
    }
    
    ///Customer Menu
    private static boolean handleCustomerMenu() {
        try {
            ioInterface.customerMenu();
            String[] input = ioInterface.getUserInput("Choose an option (1-6), for option 3 you can add keyword: ", 5);
            
            if (input.length == 0 || input[0].isEmpty()) {
                ioInterface.printErrorMessage("Customer", "Please enter a valid option.");
                return true;
            }
            
            switch (input[0]) {
                case "1":
                    // Show profile
                    handleShowProfile();
                    break;
                case "2":
                    // Update profile
                    handleUpdateProfile();
                    break;
                case "3":
                    // Show products (with optional keyword)
                    String keyword = input.length > 1 ? input[1] : "";
                    handleShowProducts("customer", keyword);
                    break;
                case "4":
                    // Show history orders
                    handleShowOrders("customer");
                    break;
                case "5":
                    // Generate consumption figures
                    handleGenerateConsumptionFigures();
                    break;
                case "6":
                    // Logout
                    currentUser = null;
                    ioInterface.printMessage("Logged out successfully.");
                    break;
                default:
                    ioInterface.printErrorMessage("Customer", "Invalid option. Please choose 1-6.");
            }
            
            return true;
            
        } catch (Exception e) {
            ioInterface.printErrorMessage("Customer", "Customer menu error: " + e.getMessage());
            return true;
        }
    }

    private static void handleShowProducts(String userRole, String... keyword) {
        try {
            if (keyword.length > 0 && !keyword[0].isEmpty()) {
                // Search with keyword
                List<Product> products = productOp.getProductListByKeyword(keyword[0]);
                ioInterface.showList(userRole, "Product", products, 1, 1);
                
                // If customer, allow ordering
                if (userRole.equals("customer") && !products.isEmpty()) {
                    handlePurchaseProduct();
                }
            } else {
                // Show paginated products
                int pageNumber = 1;
                ProductListResult result = productOp.getProductList(pageNumber);
                ioInterface.showList(userRole, "Product", result.getProductList(), 
                                   result.getCurrentPage(), result.getTotalPages());
                
                // Allow navigation through pages
                handleProductPagination(userRole, result.getTotalPages());
                if (userRole.equals("customer")) {
                    handlePurchaseProduct();
                }
            }
            
        } catch (Exception e) {
            ioInterface.printErrorMessage("Products", "Error showing products: " + e.getMessage());
        }
    }
    
    /**
     * Handle product pagination
     */
    private static void handleProductPagination(String userRole, int totalPages) {
        if (totalPages <= 1) return;
        
        try {
            String[] input = ioInterface.getUserInput("Enter page number (1-" + totalPages + ") or 'q' to quit: ", 1);
            
            if (input.length > 0 && !input[0].isEmpty() && !input[0].equals("q")) {
                try {
                    int pageNumber = Integer.parseInt(input[0]);
                    if (pageNumber >= 1 && pageNumber <= totalPages) {
                        ProductListResult result = productOp.getProductList(pageNumber);
                        ioInterface.showList(userRole, "Product", result.getProductList(), 
                                           result.getCurrentPage(), result.getTotalPages());
                        handleProductPagination(userRole, totalPages);
                    } else {
                        ioInterface.printErrorMessage("Pagination", "Invalid page number.");
                    }
                } catch (NumberFormatException e) {
                    ioInterface.printErrorMessage("Pagination", "Please enter a valid number.");
                }
            }
        } catch (Exception e) {
            ioInterface.printErrorMessage("Pagination", "Pagination error: " + e.getMessage());
        }
    }
    
    //Purchase For Customer
    private static void handlePurchaseProduct() {
        try {
            String[] input = ioInterface.getUserInput("Enter product ID to purchase (or 'q' to quit): ", 1);
            
            if (input.length > 0 && !input[0].isEmpty() && !input[0].equals("q")) {
                Product product = productOp.getProductById(input[0]);
                
                if (product != null) {
                    boolean success = orderOp.createAnOrder(currentUser.getUserId(), product.getProId(), null);
                    
                    if (success) {
                        ioInterface.printMessage("Order created successfully!");
                    } else {
                        ioInterface.printErrorMessage("Purchase", "Failed to create order.");
                    }
                } else {
                    ioInterface.printErrorMessage("Purchase", "Product not found.");
                }
            }
            
        } catch (Exception e) {
            ioInterface.printErrorMessage("Purchase", "Purchase error: " + e.getMessage());
        }
    }
    
    //Handle Customer for Admin
    private static void handleShowCustomers() {
    try {
        // Kiểm tra file tồn tại trước khi đọc
        File file = new File("data/users.txt");
        if (!file.exists() || file.length() == 0) {
            ioInterface.printMessage("No customers found in the system.");
            return;
        }

        CustomerListResult result = customerOp.getCustomerList(1); // Trang đầu tiên
        if (result.getCustomerList().isEmpty()) {
            ioInterface.printMessage("No customers found.");
        } else {
            ioInterface.showList("admin", "Customer", result.getCustomerList(),
                               result.getCurrentPage(), result.getTotalPages());
        }
    } catch (Exception e) {
        ioInterface.printErrorMessage("Show Customers", "Error: " + e.getMessage());
        e.printStackTrace(); // Log lỗi để debug
    }
}
    
    //Show Order
    private static void handleShowOrders(String userRole) {
    try {
        if (userRole.equals("admin")) {
            // Hiển thị tất cả đơn hàng cho admin
            int pageNumber = 1;
            OrderListResult result = orderOp.getAllOrders(pageNumber);
            
            if (result.getOrderList().isEmpty()) {
                ioInterface.printMessage("No orders found in the system.");
                return;
            }
            
            ioInterface.showList("admin", "Order", result.getOrderList(),
                               result.getCurrentPage(), result.getTotalPages());
            
        } else {
            // Hiển thị đơn hàng của customer hiện tại
            int pageNumber = 1;
            OrderListResult result = orderOp.getOrderList(currentUser.getUserId(), pageNumber);
            
            if (result.getOrderList().isEmpty()) {
                ioInterface.printMessage("You have no orders yet.");
                return;
            }
            
            ioInterface.showList("customer", "Order", result.getOrderList(),
                               result.getCurrentPage(), result.getTotalPages());
        }
    } catch (Exception e) {
        ioInterface.printErrorMessage("Orders", "Error displaying orders: " + e.getMessage());
    }
}

    
    
    /**
     * Handle show profile (customer only)
     */
    private static void handleShowProfile() {
        try {
            ioInterface.printObject(currentUser);
        } catch (Exception e) {
            ioInterface.printErrorMessage("Profile", "Error showing profile: " + e.getMessage());
        }
    }
    
    /**
     * Handle update profile (customer only)
     */
    private static void handleUpdateProfile() {
        try {
            ioInterface.printMessage("Available attributes: user_name, user_password, user_email, user_mobile");
            String[] input = ioInterface.getUserInput("Enter attribute name and new value (separated by space): ", 2);
            
            if (input.length < 2 || input[0].isEmpty() || input[1].isEmpty()) {
                ioInterface.printErrorMessage("Update", "Please provide both attribute name and value.");
                return;
            }
            
            boolean success = customerOp.updateProfile(input[0], input[1], (Customer) currentUser);
            
            if (success) {
                ioInterface.printMessage("Profile updated successfully!");
            } else {
                ioInterface.printErrorMessage("Update", "Failed to update profile.");
            }
            
        } catch (Exception e) {
            ioInterface.printErrorMessage("Update", "Update error: " + e.getMessage());
        }
    }
    
    /**
     * Handle generate test data (admin only)
     */
    private static void handleGenerateTestData() {
    try {
        String[] confirm = ioInterface.getUserInput(
            "This will generate test customers and orders. Continue? (y/n): ", 1);
        
        if (confirm.length > 0 && confirm[0].equalsIgnoreCase("y")) {
            // Generate test customers
            for (int i = 1; i <= 10; i++) {
                String userId = UserOperation.generateUniqueUserId();
                String username = "testuser" + i;
                String email = username + "@test.com";
                String password = "^^qwXzRtYuI7PaSd$$";
                String mobile = "04" + String.format("%08d", new Random().nextInt(100000000));
                customerOp.registerCustomer(username, "Password123!", email, mobile);
            }
            
            // Generate test orders
            orderOp.generateTestOrderData();
            
            ioInterface.printMessage("Test data generated successfully!");
            ioInterface.printMessage("- 10 test customers created");
            ioInterface.printMessage("- Random orders generated for each customer");
        }
    } catch (Exception e) {
        ioInterface.printErrorMessage("TestData", "Error generating test data: " + e.getMessage());
    }
}
    
    /**
     * Handle generate statistical figures (admin only)
     */
    private static void handleGenerateStatisticalFigures() {
    try {
        // Generate all required figures
        productOp.generateCategoryFigure();
        productOp.generateDiscountFigure();
        productOp.generateLikesCountFigure();
        productOp.generateDiscountLikesCountFigure();
        orderOp.generateAllCustomersConsumptionFigure();
        orderOp.generateAllTop10BestSellersFigure();
        
        ioInterface.printMessage("All statistical figures generated successfully!");
        ioInterface.printMessage("Check the 'data/figure' directory for generated charts.");
    } catch (Exception e) {
        ioInterface.printErrorMessage("Figures", "Error generating figures: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    /**
     * Handle generate consumption figures (customer only)
     */
    private static void handleGenerateConsumptionFigures() {
        try {
            orderOp.generateSingleCustomerConsumptionFigure(currentUser.getUserId());
            ioInterface.printMessage("Consumption figure generated successfully!");
        } catch (Exception e) {
            ioInterface.printErrorMessage("Consumption", "Error generating consumption figure: " + e.getMessage());
        }
    }
    
    /**
     * Handle delete all data (admin only)
     */
    private static void handleDeleteAllData() {
        try {
            String[] input = ioInterface.getUserInput("Are you sure? Type 'YES' to confirm: ", 1);
            
            if (input.length > 0 && "YES".equals(input[0])) {
                customerOp.deleteAllCustomers();
                productOp.deleteAllProducts();
                orderOp.deleteAllOrders();
                
                ioInterface.printMessage("All data deleted successfully!");
            } else {
                ioInterface.printMessage("Operation cancelled.");
            }
            
        } catch (Exception e) {
            ioInterface.printErrorMessage("Delete", "Error deleting data: " + e.getMessage());
        }
    }
}