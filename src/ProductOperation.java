public class ProductOperation {
    
    private static ProductOperation instance;

    
    private ProductOperation() {
      
    }
    public static ProductOperation getInstance() {
        if (instance == null) {
            instance = new ProductOperation();
        }
        return instance;
    }
}