public class Product {
    private String proId;
    private String proModel;
    private String proCategory;
    private String proName;
    private double proCurrentPrice;
    private double proRawPrice;
    private double proDiscount;
    private double proLikesCount;

    public Product(String proId, String proModel, String proCategory,String proName, 
    double proCurrentPrice, double proRawPrice,double proDiscount, int proLikesCount) {
        this.proId=proId;
        this.proModel=proModel;
        this.proCategory=proCategory;
        this.proName=proName;
        this.proCurrentPrice=proCurrentPrice;
        this.proRawPrice=proRawPrice;
        this.proDiscount=proDiscount;
        this.proLikesCount=proLikesCount;
    }

    public Product() {
        this.proId = "";
        this.proModel = "";
        this.proCategory = "";
        this.proName = "";
        this.proCurrentPrice = 0.0;
        this.proRawPrice = 0.0;
        this.proDiscount = 0.0;
        this.proLikesCount = 0;
    }

    @Override
   public String toString() {
   return "{" 
        + "\"pro_id\": \"" + getProId() + "\", "
        + "\"pro_model\": \"" + getProModel() + "\", "
        + "\"pro_category\": \"" + getProCategory() + "\", "
        + "\"pro_name\": \"" + getProName() + "\", "
        + "\"pro_current_price\": \"" + getProCurrentPrice() + "\","
        + "\"pro_raw_price\": \"" + getProRawPrice() + "\", "
        + "\"pro_discount\": \"" + getProDiscount() + "\", "
        + "\"pro_likes_count\": \"" + getProLikesCount() + "\" "
        + "}";
   }

   //Getter and setter
   public String getProCategory() {
       return proCategory;
   }
   public double getProCurrentPrice() {
       return proCurrentPrice;
   }
   public double getProDiscount() {
       return proDiscount;
   }
   public String getProId() {
       return proId;
   }
   public double getProLikesCount() {
       return proLikesCount;
   }
   public String getProModel() {
       return proModel;
   }
   public String getProName() {
       return proName;
   }
   public double getProRawPrice() {
       return proRawPrice;
   }
   public void setProCategory(String proCategory) {
       this.proCategory = proCategory;
   }
   public void setProCurrentPrice(double proCurrentPrice) {
       this.proCurrentPrice = proCurrentPrice;
   }
   public void setProDiscount(double proDiscount) {
       this.proDiscount = proDiscount;
   }
   public void setProId(String proId) {
       this.proId = proId;
   }
   public void setProLikesCount(double proLikesCount) {
       this.proLikesCount = proLikesCount;
   }
   public void setProModel(String proModel) {
       this.proModel = proModel;
   }
   public void setProName(String proName) {
       this.proName = proName;
   }
   public void setProRawPrice(double proRawPrice) {
       this.proRawPrice = proRawPrice;
   }

}