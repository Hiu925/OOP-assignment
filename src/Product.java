public class Product {
    private String proId;
    private String proModel;
    private String proCategory;
    private String proName;
    private double proCurrentPrice;
    private double proRawPrice;
    private double proDiscount;
    private int proLikesCount;

    public Product(String proId, String proModel, String proCategory,
 String proName, double proCurrentPrice, double proRawPrice,
 double proDiscount, int proLikesCount) {
   this.proId=proId;
   this.proModel=proModel;
   this.proCategory= proCategory;
   this.proName=proName;
   this.proCurrentPrice= proCurrentPrice;
   this.proRawPrice=proRawPrice;
   this.proDiscount=proDiscount;
   this.proLikesCount=proLikesCount;
 }
/**
* Default constructor
*/
public Product() {
 // Implementation with default values
}
@Override
public String toString() {
          return  "{"
                  +"\"pro_id\":" +"\""+proId+"\","
                  +"\"pro_model\":" +"\""+proModel+"\","
                  +"\"pro_category\":" +"\""+proCategory+"\","
                  +"\"pro_name\":" +"\""+proName+"\","
                  +"\"pro_current_price\":" +"\""+ proCurrentPrice+"\","
                  +"\"pro_raw_price\":" +"\""+proRawPrice+"\","
                  +"\"pro_discount\":" +"\""+proDiscount+"\","
                  +"\"pro_likes_count\":" +"\""+proLikesCount+"\","
                  +"}";
                }


}
