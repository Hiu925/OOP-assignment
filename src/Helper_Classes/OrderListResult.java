package Helper_Classes;

import Model.Order;
import java.util.List;

public class OrderListResult {
    private List<Order> orderList;
    private int currentPage;
    private int totalPages;

    public OrderListResult(List<Order> orderList, int currentPage, int totalPages) {
        this.orderList = orderList;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    // Getters
    public List<Order> getOrderList(String userId, int page) {
        return orderList;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    
}
