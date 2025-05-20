package Operation;
import Helper_Classes.*;
import Model.*;

import java.io.*;
import java.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ProductOperation {
    private static ProductOperation instance;
    private static final String PRODUCT_FILE_PATH = "data/products.txt";
    private static final String FIGURE_FOLDER_PATH = "data/figure/";
    private static final int PAGE_SIZE = 10;
    

    // Create directories if they don't exist
    private void ensureDirectoriesExist() {
        File figureFolder = new File(FIGURE_FOLDER_PATH);
        if (!figureFolder.exists()) {
            figureFolder.mkdirs();
        }
        
        File productFile = new File(PRODUCT_FILE_PATH);
        if (!productFile.getParentFile().exists()) {
            productFile.getParentFile().mkdirs();
        }
    }

    // Singleton
    public static ProductOperation getInstance() {
        if (instance == null) {
            instance = new ProductOperation();
        }
        return instance;
    }
    // 2.9.2. extractProductsFromFiles()
    
    

    // 2.9.2 test
    private Map<String, ArrayList<Object>> data = new HashMap<>();
    
    public void extractProductsFromFiles() {
    try {
        // First check if file exists
        File file = new File(PRODUCT_FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            System.out.println("No products file found or file is empty");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCT_FILE_PATH))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                try {
                    if (currentLine.trim().isEmpty()) continue;
                    
                    JSONObject jsObj = new JSONObject(currentLine);
                    ArrayList<Object> line = new ArrayList<>();
                    line.add(jsObj.getString("pro_model"));
                    line.add(jsObj.getString("pro_category"));
                    line.add(jsObj.getString("pro_name"));
                    line.add(jsObj.getDouble("pro_current_price"));
                    line.add(jsObj.getDouble("pro_raw_price"));
                    line.add(jsObj.getDouble("pro_discount"));
                    line.add(jsObj.getInt("pro_likes_count"));
                    data.put(jsObj.getString("pro_id"), line);
                } catch (Exception lineEx) {
                    System.err.println("Error parsing line: " + currentLine);
                    lineEx.printStackTrace();
                }
            }
        }
    } catch (Exception e) {
        System.err.println("Error reading products file: " + e.getMessage());
        e.printStackTrace();
    }
}

    // 2.9.3. getProductList()

    public ProductListResult getProductList(int pageNumber) {
    List<Product> allProducts = new ArrayList<>();
    File file = new File(PRODUCT_FILE_PATH);

    if (!file.exists() || file.length() == 0) {
        System.err.println("products.txt not found or empty.");
        return new ProductListResult(Collections.emptyList(), pageNumber, 0);
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCT_FILE_PATH))) {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JSONObject productJson = new JSONObject(line);
                // Convert JSONObject to Product object
                Product product = convertJsonToProduct(productJson);
                if (product != null) {
                    allProducts.add(product);
                }
            } catch (JSONException e) {
                System.err.println("Error parsing JSON line: " + line);
                e.printStackTrace();
            }
        }
    } catch (FileNotFoundException e) {
        System.err.println("Products file not found: " + PRODUCT_FILE_PATH);
        e.printStackTrace();
        return new ProductListResult(Collections.emptyList(), pageNumber, 0);
    } catch (IOException e) {
        System.err.println("Error reading products file");
        e.printStackTrace();
        return new ProductListResult(Collections.emptyList(), pageNumber, 0);
    }

    int totalPages = (int) Math.ceil((double) allProducts.size() / PAGE_SIZE);
    
    // Validate page number
    if (pageNumber < 1 || (pageNumber > totalPages && totalPages > 0)) {
        return new ProductListResult(Collections.emptyList(), pageNumber, totalPages);
    }

    int fromIndex = (pageNumber - 1) * PAGE_SIZE;
    int toIndex = Math.min(fromIndex + PAGE_SIZE, allProducts.size());

    List<Product> pageItems = allProducts.subList(fromIndex, toIndex);
    return new ProductListResult(pageItems, pageNumber, totalPages);
}

private Product convertJsonToProduct(JSONObject productJson) {
    try {
        Product product = new Product();
        product.setProId(productJson.getString("pro_id"));
        product.setProModel(productJson.getString("pro_model"));
        product.setProCategory(productJson.getString("pro_category"));
        product.setProName(productJson.getString("pro_name"));
        product.setProCurrentPrice(productJson.getDouble("pro_current_price"));
        product.setProRawPrice(productJson.getDouble("pro_raw_price"));
        product.setProDiscount(productJson.getDouble("pro_discount"));
        product.setProLikesCount(productJson.getDouble("pro_likes_count"));
        return product;
    } catch (JSONException e) {
        System.err.println("Error converting JSON to Product: " + productJson);
        e.printStackTrace();
        return null;
    }
}

    // Helper method to parse numbers from JSON
    private double parseJsonNumber(Object value) {
        if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof String) {
            return Double.parseDouble((String) value);
        } else {
            throw new NumberFormatException("Cannot parse value: " + value);
        }
    }
     
    // 2.9.4. deleteProduct()
    public boolean deleteProduct(String productId) {
    File inputFile = new File(PRODUCT_FILE_PATH);
    File tempFile = new File(PRODUCT_FILE_PATH + ".tmp");
    
    boolean productFound = false;
    
    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
        
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JSONObject productJson = new JSONObject(line);
                if (!productJson.getString("product_id").equals(productId)) {
                    // Write all products except the one to delete
                    writer.write(line);
                    writer.newLine();
                } else {
                    productFound = true;
                }
            } catch (JSONException e) {
                System.err.println("Error parsing JSON line: " + line);
                e.printStackTrace();
                // Keep the line in the file even if it can't be parsed
                writer.write(line);
                writer.newLine();
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
    
    // Only replace original file if product was found
    if (productFound) {
        // Delete original file
        if (!inputFile.delete()) {
            System.err.println("Could not delete original file");
            return false;
        }
        
        // Rename temp file to original file name
        if (!tempFile.renameTo(inputFile)) {
            System.err.println("Could not rename temp file");
            return false;
        }
    } else {
        // Delete temp file if no changes were made
        tempFile.delete();
    }
    
    return productFound;
}

    // 2.9.5. getProductListByKeyword()
    public List<Product> getProductListByKeyword(String keyword) {
    List<Product> matchingProducts = new ArrayList<>();
    
    if (keyword == null || keyword.trim().isEmpty()) {
        return matchingProducts;
    }
    
    String searchTerm = keyword.toLowerCase();
    
    try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCT_FILE_PATH))) {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JSONObject productJson = new JSONObject(line);
                String productName = productJson.getString("product_name").toLowerCase();
                if (productName.contains(searchTerm)) {
                    matchingProducts.add(convertJsonToProduct(productJson));
                }
            } catch (JSONException e) {
                System.err.println("Error parsing JSON line: " + line);
                e.printStackTrace();
            }
        }
    } catch (FileNotFoundException e) {
        System.err.println("Products file not found: " + PRODUCT_FILE_PATH);
        e.printStackTrace();
    } catch (IOException e) {
        System.err.println("Error reading products file");
        e.printStackTrace();
    }
    
    return matchingProducts;
}

    // 2.9.6. getProductById()
    public Product getProductById(String productId) {
    if (productId == null || productId.trim().isEmpty()) {
        return null;
    }
    
    try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCT_FILE_PATH))) {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JSONObject productJson = new JSONObject(line);
                if (productJson.getString("product_id").equals(productId)) {
                    return convertJsonToProduct(productJson);
                }
            } catch (JSONException e) {
                System.err.println("Error parsing JSON line: " + line);
                e.printStackTrace();
            }
        }
    } catch (FileNotFoundException e) {
        System.err.println("Products file not found: " + PRODUCT_FILE_PATH);
        e.printStackTrace();
    } catch (IOException e) {
        System.err.println("Error reading products file");
        e.printStackTrace();
    }
    
    return null;
}

    // 2.9.7. generateCategoryFigure()
    public void generateCategoryFigure() {
        ensureDirectoriesExist();
        File file = new File(PRODUCT_FILE_PATH);
        Map<String, Integer> categoryCountMap = new HashMap<>();

        // Step 1: Count products by category
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(file));
            JSONArray jsonProducts = (JSONArray) obj;

            for (Object o : jsonProducts) {
                JSONObject jsonProduct = (JSONObject) o;
                String category = (String) jsonProduct.get("pro_category");
                categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error reading products.txt: " + e.getMessage());
            return;
        }

        // Step 2: Sort categories by count (descending)
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(categoryCountMap.entrySet());
        sortedEntries.sort((a, b) -> b.getValue() - a.getValue());

        // Step 3: Prepare dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            dataset.addValue(entry.getValue(), "Products", entry.getKey());
        }

        // Step 4: Create the bar chart
        JFreeChart barChart = ChartFactory.createBarChart(
                "Product Count by Category",
                "Category",
                "Number of Products",
                dataset
        );

        // Step 5: Save the chart as PNG
        try {
            File chartFile = new File(FIGURE_FOLDER_PATH, "category_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, barChart, 800, 600);
            System.out.println("Category chart saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving chart: " + e.getMessage());
        }
    }

    // 2.9.8. generateDiscountFigure()
    public void generateDiscountFigure() {
        ensureDirectoriesExist();
        File file = new File(PRODUCT_FILE_PATH);

        // Counters for each discount range
        int lessThan30 = 0;
        int between30And60 = 0;
        int greaterThan60 = 0;

        // Step 1: Count products by discount range
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(file));
            JSONArray jsonProducts = (JSONArray) obj;

            for (Object o : jsonProducts) {
                JSONObject jsonProduct = (JSONObject) o;
                try {
                    double discount = parseJsonNumber(jsonProduct.get("pro_discount"));
                    
                    if (discount < 30.0) {
                        lessThan30++;
                    } else if (discount <= 60.0) {
                        between30And60++;
                    } else {
                        greaterThan60++;
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing discount value: " + jsonProduct.get("pro_discount"));
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error reading products.txt: " + e.getMessage());
            return;
        }

        // Step 2: Create the dataset for the pie chart
        org.jfree.data.general.DefaultPieDataset dataset = new org.jfree.data.general.DefaultPieDataset();
        dataset.setValue("Less than 30%", lessThan30);
        dataset.setValue("30% to 60%", between30And60);
        dataset.setValue("Greater than 60%", greaterThan60);

        // Step 3: Create the pie chart
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Product Distribution by Discount Range",
                dataset,
                true, // include legend
                true,
                false
        );

        // Step 4: Save the chart as PNG
        try {
            File chartFile = new File(FIGURE_FOLDER_PATH, "discount_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, pieChart, 800, 600);
            System.out.println("Discount chart saved to " + chartFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving discount chart: " + e.getMessage());
        }
    }

    // 2.9.9. generateLikesCountFigure()
    public void generateLikesCountFigure() {
        ensureDirectoriesExist();
        File file = new File(PRODUCT_FILE_PATH);
        Map<String, Integer> categoryLikesMap = new HashMap<>();

        // Step 1: Sum likes_count by category
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(file));
            JSONArray jsonProducts = (JSONArray) obj;

            for (Object o : jsonProducts) {
                JSONObject jsonProduct = (JSONObject) o;
                try {
                    String category = (String) jsonProduct.get("pro_category");
                    
                    // Handle likes count (may have trailing space in JSON key)
                    int likesCount;
                    if (jsonProduct.getString("pro_likes_count")!=null) {
                        likesCount = (int) parseJsonNumber(jsonProduct.get("pro_likes_count"));
                    } else if (jsonProduct.getString("pro_likes_count ")!=null) {
                        likesCount = (int) parseJsonNumber(jsonProduct.get("pro_likes_count "));
                    } else {
                        likesCount = 0;
                    }
                    
                    // Add likes to category total
                    categoryLikesMap.put(category, categoryLikesMap.getOrDefault(category, 0) + likesCount);
                } catch (Exception e) {
                    System.err.println("Error parsing likes count for product: " + jsonProduct);
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error reading products.json: " + e.getMessage());
            return;
        }

        // Step 2: Sort categories by likes count (ascending)
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(categoryLikesMap.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue());

        // Step 3: Prepare dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            dataset.addValue(entry.getValue(), "Total Likes", entry.getKey());
        }

        // Step 4: Create the bar chart
        JFreeChart barChart = ChartFactory.createBarChart(
                "Total Likes Count by Category",
                "Category",
                "Total Likes",
                dataset
        );

        // Step 5: Save the chart as PNG
        try {
            File chartFile = new File(FIGURE_FOLDER_PATH, "likes_count_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, barChart, 800, 600);
            System.out.println("Likes count chart saved to " + chartFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving likes count chart: " + e.getMessage());
        }
    }

    // 2.9.10. generateDiscountLikesCountFigure()
    public void generateDiscountLikesCountFigure() {
        ensureDirectoriesExist();
        File file = new File(PRODUCT_FILE_PATH);

        // Step 1: Collect discount and likes count data for each product
        org.jfree.data.xy.XYSeries series = new org.jfree.data.xy.XYSeries("Products");
        org.jfree.data.xy.XYSeriesCollection dataset = new org.jfree.data.xy.XYSeriesCollection();
        dataset.addSeries(series);

        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(file));
            JSONArray jsonProducts = (JSONArray) obj;

            for (Object o : jsonProducts) {
                JSONObject jsonProduct = (JSONObject) o;
                try {
                    double discount = parseJsonNumber(jsonProduct.get("pro_discount"));
                    
                    // Handle likes count (may have trailing space in JSON key)
                    int likesCount;
                    if (jsonProduct.getString("pro_likes_count")!=null) {
                        likesCount = (int) parseJsonNumber(jsonProduct.get("pro_likes_count"));
                    } else if (jsonProduct.getString("pro_likes_count ")!=null) {
                        likesCount = (int) parseJsonNumber(jsonProduct.get("pro_likes_count "));
                    } else {
                        likesCount = 0;
                    }
                    
                    // Add data point to the dataset
                    series.add(discount, likesCount);
                } catch (Exception e) {
                    System.err.println("Error parsing data for discount-likes chart: " + jsonProduct);
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error reading products.json: " + e.getMessage());
            return;
        }

        // Step 2: Create the scatter plot
        JFreeChart scatterChart = ChartFactory.createScatterPlot(
                "Relationship Between Discount and Likes Count",
                "Discount (%)",
                "Likes Count",
                dataset
        );

        // Step 3: Save the chart as PNG
        try {
            File chartFile = new File(FIGURE_FOLDER_PATH, "discount_likes_chart.png");
            ChartUtils.saveChartAsPNG(chartFile, scatterChart, 800, 600);
            System.out.println("Discount-Likes correlation chart saved to " + chartFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving discount-likes chart: " + e.getMessage());
        }
    }

    // 2.9.11. deleteAllProducts()
    public void deleteAllProducts() {
        File productFile = new File(PRODUCT_FILE_PATH);
        
        if (!productFile.exists()) {
            System.out.println("No products file found to delete.");
            return;
        }
        
        try {
            // Create an empty JSON array file
            JSONArray emptyArray = new JSONArray();
            try (FileWriter writer = new FileWriter(productFile)) {
                writer.write(emptyArray.toJSONString());
                System.out.println("All product data has been deleted successfully.");
            }
        } catch (IOException e) {
            System.err.println("Error deleting product data: " + e.getMessage());
        }
    }
}