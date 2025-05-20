package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Order {
    private String orderId;    
    private String userId;   
    private String proId;    
    private String orderTime;

    public Order(String oderId, String userId, String proId, String orderTime){
        //Check oderID
        if (!oderId.matches("^o_\\d{5}$")) {
            System.out.println("Invalid oderID, Expected format: o_{5 number digits}. So auto set is o_00000");
            this.userId="o_00000";
        } else this.userId = userId;
        //Check userID
        if (!userId.matches("^u_\\d{10}$")) {
            System.out.println("Invalid userID, Expected format: u_{10 digits}. So auto set is u_0000000000");
            this.userId="u_0000000000";
        } else this.userId = userId;

        this.proId=proId;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        try {
            // Thử parse để kiểm tra định dạng
            LocalDateTime.parse(orderTime, formatter);
            this.orderTime = orderTime;
        } catch (DateTimeParseException e) {
            // Nếu sai định dạng thì gán mặc định
            System.out.printf("Invalid time register of user %s, auto set to default 01-01-2000_00:00:00", userId);
            this.orderTime = "01-01-2000_00:00:00";
        }

    }

    public Order() {
        this.orderId = "o_00000";
        this.userId = "";
        this.proId = "";
        this.orderTime = "01-01-2000_00:00:00";
    }

    @Override
    public String toString() {
        return "{" 
             + "\"order_id\":\"" + orderId + "\", "
             + "\"user_id\":\"" + userId + "\", "
             + "\"pro_id\":\"" + proId + "\", "
             + "\"order_time\":\"" + orderTime + "\""
             + "}";
    }

    //Getter and Setter
    public String getOrderId() {
        return orderId;
    }
    public String getOrderTime() {
        return orderTime;
    }
    public String getProId() {
        return proId;
    }
    public String getUserId() {
        return userId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
    public void setProId(String proId) {
        this.proId = proId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

}