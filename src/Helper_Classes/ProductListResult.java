package Helper_Classes;

import Model.Product;
import java.util.List;

public class ProductListResult {
    private List<Product> productList;
    private int currentPage;
    private int totalPages;

    public ProductListResult(List<Product> productList, int currentPage, int totalPages) {
        this.productList = productList;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    // Getters
    public List<Product> getProductList() {
        return productList;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }
}