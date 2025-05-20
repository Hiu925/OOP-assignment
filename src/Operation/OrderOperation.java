package Operation;
import Model.*;
import Helper_Classes.*;

import java.time.*;
import java.io.*;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contains all the operations related to the order.
 * This class follows the Singleton pattern.
 */
public class OrderOperation {
    private static OrderOperation instance;
    private static final String ORDER_DATA_FILE = "data/orders.txt";
    private static final String PRODUCT_DATA_FILE = "data/products.txt";
    private static final String USER_DATA_FILE = "data/users.txt";
    private static final int PAGE_SIZE = 10;
    private static final Random random = new Random();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
    
    private OrderOperation() {
        // Ensure the data directory exists
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        // Ensure the figure directory exists
        File figureDir = new File("data/figure");
        if (!figureDir.exists()) {
            figureDir.mkdirs();
        }
    }
    
    ///Singleton
    public static OrderOperation getInstance() {
        if (instance == null) {
            instance = new OrderOperation();
        }
        return instance;
    }
    
    //Unique ID
    public String generateUniqueOrderId() {
        String orderId;
        do {
            // Generate a 5-digit random number
            int num = 10000 + random.nextInt(90000);
            orderId = "o_" + num;
        } while (orderIdExists(orderId)); // Ensure uniqueness
        
        return orderId;
    }

    private boolean orderIdExists(String orderId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JSONObject orderJson = new JSONObject(line);
                    if (orderJson.getString("order_id").equals(orderId)) {
                        return true;
                    }
                } catch (JSONException e) {
                    // Skip malformed lines
                }
            }
        } catch (IOException e) {
            // If file doesn't exist or can't be read, assume orderId doesn't exist
        }
        return false;
    }
    
    //Create Order
    public boolean createAnOrder(String customerId, String productId, String createTime) {
        if (customerId == null || productId == null) {
            return false;
        }

        String orderId = generateUniqueOrderId();
        String orderTime = (createTime == null) ? 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) : 
            createTime;

        JSONObject orderJson = new JSONObject();
        try {
            orderJson.put("order_id", orderId);
            orderJson.put("customer_id", customerId);
            orderJson.put("product_id", productId);
            orderJson.put("order_time", orderTime);
            orderJson.put("status", "pending"); // Default status
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        // Append to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_DATA_FILE, true))) {
            writer.write(orderJson.toString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Delete Order
    public boolean deleteOrder(String orderId) {
        File inputFile = new File(ORDER_DATA_FILE);
        File tempFile = new File(ORDER_DATA_FILE + "tmp.txt");
        
        boolean orderFound = false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JSONObject orderJson = new JSONObject(line);
                    if (!orderJson.getString("order_id").equals(orderId)) {
                        writer.write(line);
                        writer.newLine();
                    } else {
                        orderFound = true;
                    }
                } catch (JSONException e) {
                    // Keep malformed lines
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        if (orderFound) {
            // Replace original file
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                return false;
            }
        } else {
            tempFile.delete();
        }
        
        return orderFound;
    }

    

    //Get Order List
    public OrderListResult getOrderList(String customerId, int pageNumber) {
        List<Order> allOrders = new ArrayList<>();
        
        // Read all orders for this customer
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JSONObject orderJson = new JSONObject(line);
                    if (orderJson.getString("user_id").equals(customerId)) {
                        allOrders.add(convertJsonToOrder(orderJson));
                    }
                } catch (JSONException e) {
                    // Skip malformed lines
                }
            }
        } catch (IOException e) {
            return new OrderListResult(Collections.emptyList(), pageNumber, 0);
        }
        
        // Calculate pagination
        int totalPages = (int) Math.ceil((double) allOrders.size() / PAGE_SIZE);
        
        // Validate page number
        if (pageNumber < 1 || (pageNumber > totalPages && totalPages > 0)) {
            return new OrderListResult(Collections.emptyList(), pageNumber, totalPages);
        }
        
        // Get sublist for current page
        int fromIndex = (pageNumber - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, allOrders.size());
        List<Order> pageOrders = allOrders.subList(fromIndex, toIndex);
        
        return new OrderListResult(pageOrders, pageNumber, totalPages);
    }
    private Order convertJsonToOrder(JSONObject orderJson) throws JSONException {
        Order order = new Order();
        order.setOrderId(orderJson.getString("order_id"));
        order.setUserId(orderJson.getString("user_id"));
        order.setProId(orderJson.getString("pro_id"));
        order.setOrderTime(orderJson.getString("order_time"));
        return order;
    }
    
    public void generateTestOrderData() {
    // Create test customers
    List<String> customerIds = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
        String customerId = UserOperation.generateUniqueUserId();
        customerIds.add(customerId);
        
        // Create customer JSON (you might want to save this to customers.txt)
        JSONObject customer = new JSONObject();
        customer.put("user_id", customerId);
        customer.put("name", "TestCustomer" + i);
        customer.put("email", "customer" + i + "@test.com");
        // Add other customer fields as needed
    }

    

    // Generate random orders
    Random random = new Random();
    String[] products = {"p001", "p002", "p003", "p004", "p005", "p006", "p007", "p008", "p009", "p010"};

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_DATA_FILE, true))) {
        writer.newLine();
        for (String customerId : customerIds) {
            int orderCount = 50 + random.nextInt(151); // 50-200 orders
            
            for (int i = 0; i < orderCount; i++) {
                JSONObject order = new JSONObject();
                order.put("order_id", generateUniqueOrderId());
                order.put("user_id", customerId);
                
                // Random product selection
                int productIndex = random.nextInt(products.length);
                order.put("pro_id", products[productIndex]);
                
                // Random date within a year
                int month = 1 + random.nextInt(12);
                int day = 1 + random.nextInt(28); // Simple approach
                LocalDateTime orderTime = LocalDateTime.of(2023, month, day, 
                    random.nextInt(24), random.nextInt(60));
                
                order.put("order_time", orderTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
                
                writer.write(order.toString());
                writer.newLine();
            }
        }
        System.out.println("Generated test order data successfully");
    } catch (IOException e) {
        System.err.println("Error generating test order data");
        e.printStackTrace();
    }
}

    public void generateSingleCustomerConsumptionFigure(String customerId) {
    // Create figure directory if it doesn't exist
    File figureDir = new File("data/figure");
    if (!figureDir.exists()) {
        figureDir.mkdirs();
    }

    // Initialize monthly totals
    Map<Integer, Double> monthlyTotals = new HashMap<>();
    for (int i = 1; i <= 12; i++) {
        monthlyTotals.put(i, 0.0);
    }

    // Read and process orders
    try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_DATA_FILE))) {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JSONObject order = new JSONObject(line);
                if (order.getString("customer_id").equals(customerId)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    LocalDateTime orderTime = LocalDateTime.parse(order.getString("order_time"), formatter);
                    int month = orderTime.getMonthValue();
                    double price = order.getDouble("price");
                    
                    monthlyTotals.put(month, monthlyTotals.get(month) + price);
                }
            } catch (JSONException e) {
                // Skip malformed orders
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading orders file");
        e.printStackTrace();
        return;
    }

    // Create dataset
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (int month = 1; month <= 12; month++) {
        dataset.addValue(monthlyTotals.get(month), "Consumption", 
            Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault()));
    }

    // Create chart
    JFreeChart chart = ChartFactory.createBarChart(
        "Monthly Consumption for Customer " + customerId,
        "Month",
        "Total Amount ($)",
        dataset
    );

    // Customize chart
    chart.setBackgroundPaint(Color.white);
    CategoryPlot plot = chart.getCategoryPlot();
    plot.setBackgroundPaint(Color.lightGray);
    plot.setRangeGridlinePaint(Color.white);

    // Save chart
    try {
        String fileName = "customer_" + customerId + "_consumption.png";
        ChartUtils.saveChartAsPNG(new File(figureDir, fileName), chart, 800, 600);
        System.out.println("Chart saved to " + fileName);
    } catch (IOException e) {
        System.err.println("Error saving chart");
        e.printStackTrace();
    }
}

    public void generateAllCustomersConsumptionFigure() {
    // Create figure directory if it doesn't exist
    File figureDir = new File("data/figure");
    if (!figureDir.exists()) {
        figureDir.mkdirs();
    }

    // Structure: Month -> Total Consumption
    Map<Integer, Double> monthlyTotals = new HashMap<>();
    for (int i = 1; i <= 12; i++) {
        monthlyTotals.put(i, 0.0);
    }

    // Read and process all orders
    try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_DATA_FILE))) {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JSONObject order = new JSONObject(line);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                LocalDateTime orderTime = LocalDateTime.parse(order.getString("order_time"), formatter);
                int month = orderTime.getMonthValue();
                double price = order.getDouble("price");
                
                monthlyTotals.put(month, monthlyTotals.get(month) + price);
            } catch (JSONException e) {
                // Skip malformed orders
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading orders file");
        e.printStackTrace();
        return;
    }

    // Create dataset
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (int month = 1; month <= 12; month++) {
        dataset.addValue(monthlyTotals.get(month), "Consumption", 
            Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault()));
    }

    // Create chart
    JFreeChart chart = ChartFactory.createBarChart(
        "Monthly Consumption for All Customers",
        "Month",
        "Total Amount ($)",
        dataset
    );

    // Customize chart
    chart.setBackgroundPaint(Color.white);
    CategoryPlot plot = chart.getCategoryPlot();
    plot.setBackgroundPaint(Color.lightGray);
    plot.setRangeGridlinePaint(Color.white);

    // Save chart
    try {
        ChartUtils.saveChartAsPNG(new File(figureDir, "all_customers_consumption.png"), chart, 800, 600);
        System.out.println("Chart saved to all_customers_consumption.png");
    } catch (IOException e) {
        System.err.println("Error saving chart");
        e.printStackTrace();
    }
}

public void generateAllTop10BestSellersFigure() {
    // Create figure directory if it doesn't exist
    File figureDir = new File("data/figure");
    if (!figureDir.exists()) {
        figureDir.mkdirs();
    }

    // Track product sales
    Map<String, Integer> productSales = new HashMap<>();

    // Read and process all orders
    try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_DATA_FILE))) {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JSONObject order = new JSONObject(line);
                String productId = order.getString("product_id");
                productSales.put(productId, productSales.getOrDefault(productId, 0) + 1);
            } catch (JSONException e) {
                // Skip malformed orders
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading orders file");
        e.printStackTrace();
        return;
    }

    // Sort products by sales (descending) and get top 10
    List<Map.Entry<String, Integer>> sortedProducts = productSales.entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .limit(10)
        .collect(Collectors.toList());

    // Create dataset
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (Map.Entry<String, Integer> entry : sortedProducts) {
        dataset.addValue(entry.getValue(), "Sales", entry.getKey());
    }

    // Create chart
    JFreeChart chart = ChartFactory.createBarChart(
        "Top 10 Best Selling Products",
        "Product ID",
        "Number of Sales",
        dataset
    );

    // Customize chart
    chart.setBackgroundPaint(Color.white);
    CategoryPlot plot = chart.getCategoryPlot();
    plot.setBackgroundPaint(Color.lightGray);
    plot.setRangeGridlinePaint(Color.white);

    // Save chart
    try {
        ChartUtils.saveChartAsPNG(new File(figureDir, "top10_products.png"), chart, 800, 600);
        System.out.println("Chart saved to top10_products.png");
    } catch (IOException e) {
        System.err.println("Error saving chart");
        e.printStackTrace();
    }
}

public void deleteAllOrders() {
    try {
        // Simply open and close the file in write mode to truncate it
        new FileWriter(ORDER_DATA_FILE, false).close();
        System.out.println("All orders have been deleted");
    } catch (IOException e) {
        System.err.println("Error deleting orders");
        e.printStackTrace();
    }
}

    public OrderListResult getAllOrders(int pageNumber) {
    List<Order> allOrders = new ArrayList<>();
    
    try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_DATA_FILE))) {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JSONObject orderJson = new JSONObject(line);
                Order order = convertJsonToOrder(orderJson);
                allOrders.add(order);
            } catch (JSONException e) {
                System.err.println("Error parsing order: " + line);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    // Phân trang
    int totalPages = (int) Math.ceil((double) allOrders.size() / PAGE_SIZE);
    int fromIndex = (pageNumber - 1) * PAGE_SIZE;
    int toIndex = Math.min(fromIndex + PAGE_SIZE, allOrders.size());
    
    return new OrderListResult(
        allOrders.subList(fromIndex, toIndex),
        pageNumber,
        totalPages
    );
}

}
