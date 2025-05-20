

import java.io.*;
import java.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class ProductOperation {
    private static ProductOperation instance;
    private static final String PRODUCT_FILE_PATH = "data/products.txt";
    private static final String FIGURE_FOLDER_PATH = "data/figure/";
    

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

    //Singleton
    public static ProductOperation getInstance() {
        if (instance == null) {
            instance = new ProductOperation();
        }
        return instance;
    }

// 2.9.2. extractProductsFromFiles()
    public void extractProductsFromFiles() {
        ensureDirectoriesExist();
        File inputFolder = new File("input");
        File outputFile = new File(PRODUCT_FILE_PATH);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            File[] productFiles = inputFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

            if (productFiles == null || productFiles.length == 0) {
                System.out.println("No product files found in the 'input' directory.");
                return;
            }

            for (File productFile : productFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(productFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.trim().isEmpty()) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error reading file: " + productFile.getName());
                }
            }
            System.out.println("Product information extracted successfully.");
        } catch (IOException e) {
            System.err.println("Error writing to products file: " + e.getMessage());
        }
    }

    //2.9.3. getProductList()

    private static final int PAGE_SIZE = 10;// them variable

    public ProductListResult getProductList(int pageNumber) {
        List<Product> allProducts = new ArrayList<>();
        File file = new File(PRODUCT_FILE_PATH);

        if (!file.exists()) {
            System.err.println("products.txt not found.");
            return new ProductListResult(Collections.emptyList(), pageNumber, 0);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");
                if (parts.length == 8) {
                    try {
                        String proId = parts[0].trim();
                        String proModel = parts[1].trim();
                        String proCategory = parts[2].trim();
                        String proName = parts[3].trim();
                        double proCurrentPrice = Double.parseDouble(parts[4].trim());
                        double proRawPrice = Double.parseDouble(parts[5].trim());
                        double proDiscount = Double.parseDouble(parts[6].trim());
                        int proLikesCount = Integer.parseInt(parts[7].trim());
                        Product product = new Product(proId, proModel, proCategory, proName,
                                proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
                        allProducts.add(product);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid line (number format issue): " + line);
                    }

                }
                else {
                    System.err.println("Skipping invalid line (wrong number of fields): " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading products.txt: " + e.getMessage());
        }

        int totalPages = (int) Math.ceil((double) allProducts.size() / PAGE_SIZE);
        if (pageNumber < 1 || pageNumber > totalPages) {
            return new ProductListResult(Collections.emptyList(), pageNumber, totalPages);
        }

        int fromIndex = (pageNumber - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, allProducts.size());

        List<Product> pageItems = allProducts.subList(fromIndex, toIndex);
        return new ProductListResult(pageItems, pageNumber, totalPages);
     }

     
//2.9.4. deleteProduct()

    private static final String PRODUCTS_PATH = "data/products.txt";
    private static final String TEMP_PRODUCTS_PATH = "data/products_temp.txt";

    public boolean deleteProduct(String productId) {

    boolean deleted = false;

    File inputFile = new File(PRODUCTS_PATH);
    File tempFile = new File(TEMP_PRODUCTS_PATH);


    if (!inputFile.exists()) {
        System.err.println("File not found: data/products.txt");
        return false;
    }

    try (
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) 
        {

        String line;
        
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length > 0 && parts[0].trim().equals(productId)) {
                // Skip this product (i.e., delete)
                deleted = true;
                continue;
            }
            writer.write(line);
            writer.newLine();
        }
    } catch (IOException e) {
        System.err.println("Error processing file: " + e.getMessage());
        return false;
    }

    // Replace original file with updated file
    if (!deleted) {
            tempFile.delete();
            return false;
        }

    return inputFile.delete() && tempFile.renameTo(inputFile);
}

//2.9.5. getProductListByKeyword()
public List<Product> getProductListByKeyword(String keyword) {
    List<Product> matchedProducts = new ArrayList<>();
    File file = new File(PRODUCTS_PATH);

    if (!file.exists()) {
        System.err.println("products.txt file not found.");
        return matchedProducts;
    }

    String lowerKeyword = keyword.toLowerCase();

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 8) {
                String proName = parts[3].trim().toLowerCase();  // product name
                if (proName.contains(lowerKeyword)) {
                    try {
                        String proId = parts[0].trim();
                        String proModel = parts[1].trim();
                        String proCategory = parts[2].trim();
                        double proCurrentPrice = Double.parseDouble(parts[4].trim());
                        double proRawPrice = Double.parseDouble(parts[5].trim());
                        double proDiscount = Double.parseDouble(parts[6].trim());
                        int proLikesCount = Integer.parseInt(parts[7].trim());

                        Product product = new Product(proId, proModel, proCategory, parts[3].trim(),
                                proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
                        matchedProducts.add(product);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid product line: " + line);
                    }
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading products.txt: " + e.getMessage());
    }

    return matchedProducts;
 }

//2.9.6. getProductById()
public Product getProductById(String productId) {
    File file = new File(PRODUCTS_PATH);

    if (!file.exists()) {
        System.err.println("products.txt file not found.");
        return null;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 8 && parts[0].trim().equals(productId)) {
                try {
                    String proId = parts[0].trim();
                    String proModel = parts[1].trim();
                    String proCategory = parts[2].trim();
                    String proName = parts[3].trim();
                    double proCurrentPrice = Double.parseDouble(parts[4].trim());
                    double proRawPrice = Double.parseDouble(parts[5].trim());
                    double proDiscount = Double.parseDouble(parts[6].trim());
                    int proLikesCount = Integer.parseInt(parts[7].trim());

                    return new Product(proId, proModel, proCategory, proName,
                            proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format for product: " + productId);
                    return null;
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading products.txt: " + e.getMessage());
    }

    return null; // Not found
}

//2.9.7. generateCategoryFigure()
public void generateCategoryFigure() {

    ensureDirectoriesExist();

    File inputFile = new File(PRODUCTS_PATH);
    Map<String, Integer> categoryCountMap = new HashMap<>();

    // Step 1: Count products by category
    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                String category = parts[2].trim();
                categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
            }
        }
    } catch (IOException e) {
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
        File chartFile = new File(outputDir, "category_chart.png");
        ChartUtils.saveChartAsPNG(chartFile, barChart, 800, 600);
        System.out.println("Chart saved to " + chartFile.getAbsolutePath());
    } catch (IOException e) {
        System.err.println("Error saving chart: " + e.getMessage());
    }
}

// 2.9.8. generateDiscountFigure()
public void generateDiscountFigure() {
    File inputFile = new File("data/products.txt");
    File outputDir = new File("data/figure");
    if (!outputDir.exists()) {
        outputDir.mkdirs(); // Create the folder if it doesn't exist
    }

    // Counters for each discount range
    int lessThan30 = 0;
    int between30And60 = 0;
    int greaterThan60 = 0;

    // Step 1: Count products by discount range
    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 7) {
                try {
                    double discount = Double.parseDouble(parts[6].trim());
                    
                    if (discount < 30.0) {
                        lessThan30++;
                    } else if (discount <= 60.0) {
                        between30And60++;
                    } else {
                        greaterThan60++;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing discount value: " + parts[6]);
                }
            }
        }
    } catch (IOException e) {
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
        File chartFile = new File(outputDir, "discount_chart.png");
        ChartUtils.saveChartAsPNG(chartFile, pieChart, 800, 600);
        System.out.println("Discount chart saved to " + chartFile.getAbsolutePath());
    } catch (IOException e) {
        System.err.println("Error saving discount chart: " + e.getMessage());
    }
}

// 2.9.9. generateLikesCountFigure()
public void generateLikesCountFigure() {
    File inputFile = new File("data/products.txt");
    File outputDir = new File("data/figure");
    if (!outputDir.exists()) {
        outputDir.mkdirs(); // Create the folder if it doesn't exist
    }

    Map<String, Integer> categoryLikesMap = new HashMap<>();

    // Step 1: Sum likes_count by category
    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 8) {
                try {
                    String category = parts[2].trim();
                    int likesCount = Integer.parseInt(parts[7].trim());
                    
                    // Add likes to category total
                    categoryLikesMap.put(category, categoryLikesMap.getOrDefault(category, 0) + likesCount);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing likes count: " + parts[7]);
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading products.txt: " + e.getMessage());
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
        File chartFile = new File(outputDir, "likes_count_chart.png");
        ChartUtils.saveChartAsPNG(chartFile, barChart, 800, 600);
        System.out.println("Likes count chart saved to " + chartFile.getAbsolutePath());
    } catch (IOException e) {
        System.err.println("Error saving likes count chart: " + e.getMessage());
    }
}

// 2.9.10. generateDiscountLikesCountFigure()
public void generateDiscountLikesCountFigure() {
    File inputFile = new File("data/products.txt");
    File outputDir = new File("data/figure");
    if (!outputDir.exists()) {
        outputDir.mkdirs(); // Create the folder if it doesn't exist
    }

    // Step 1: Collect discount and likes count data for each product
    // org.jfree.data.xy.XYSeries dataset = new org.jfree.data.xy.XYSeriesCollection().addSeries(new org.jfree.data.xy.XYSeries("Products"));
    org.jfree.data.xy.XYSeries series = new org.jfree.data.xy.XYSeries("Products");
    org.jfree.data.xy.XYSeriesCollection dataset = new org.jfree.data.xy.XYSeriesCollection();
    dataset.addSeries(series);

    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 8) {
                try {
                    double discount = Double.parseDouble(parts[6].trim());
                    int likesCount = Integer.parseInt(parts[7].trim());
                    
                    // Add data point to the dataset
                    series.add(discount, likesCount);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing data: " + line);
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading products.txt: " + e.getMessage());
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
        File chartFile = new File(outputDir, "discount_likes_chart.png");
        ChartUtils.saveChartAsPNG(chartFile, scatterChart, 800, 600);
        System.out.println("Discount-Likes correlation chart saved to " + chartFile.getAbsolutePath());
    } catch (IOException e) {
        System.err.println("Error saving discount-likes chart: " + e.getMessage());
    }
}

// 2.9.11. deleteAllProducts()
public void deleteAllProducts() {
    File productFile = new File("data/products.txt");
    
    if (!productFile.exists()) {
        System.out.println("No products file found to delete.");
        return;
    }
    
    try {
        // Create an empty file (overwriting the existing one)
        BufferedWriter writer = new BufferedWriter(new FileWriter(productFile));
        writer.close();
        System.out.println("All product data has been deleted successfully.");
    } catch (IOException e) {
        System.err.println("Error deleting product data: " + e.getMessage());
    }
}
 
}
