public class UserOperation {

    private static UserOperation instance;

    
    private UserOperation() {
        
    }

    public static UserOperation getInstance() {
        if (instance == null) {
            instance = new UserOperation();
        }
        return instance;
    }

    
}
