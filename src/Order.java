public class Order {
   private String orderId;
   private String userId;
   private String proId;
   private String orderTime;

   public Order(String orderId, String userId, String proId, String orderTime) {

       this.orderId=orderId;
       this.userId=userId;
       this.proId=proId;
       this.orderTime=orderTime;
}

/**
* Default constructor
*/
public Order() {
 // Implementation with default values
}
@Override
public String toString() {
       return "{"
       +"\"order_id\":\"" +orderId +"\","
       +"\"user_id\":\""+ userId +"\","
       +"\"pro_id\":\""+ proId +"\","
       +"\"order_time\":\""+ orderTime +"\","
       +"}";
}

}
