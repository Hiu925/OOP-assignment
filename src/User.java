import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public abstract class User {
    private String userId;
    private String userName;
    private String userPassword;
    private String userRegisterTime;
    private String userRole;

    public User (String userId, String userName,String userPassword, String userRegistrerTime, String userRole){

        this.userName= userName;
        this.userPassword= userPassword;
        
        //Check userID
        if (!userId.matches("^u_\\d{10}$")) {
            System.out.println("Invalid userID, Expected format: u_{10 digits}. So auto set is u_0000000000");
            this.userId="u_0000000000";
        } else this.userId = userId;
        
        // Validate date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        try {
            // Thử parse để kiểm tra định dạng
            LocalDateTime.parse(userRegisterTime, formatter);
            this.userRegisterTime = userRegisterTime;
        } catch (DateTimeParseException e) {
            // Nếu sai định dạng thì gán mặc định
            System.out.printf("Invalid time register of user %s, auto set to default 01-01-2000_00:00:00", userId);
            this.userRegisterTime = "01-01-2000_00:00:00";
        }

        // Check userRole
        userRole=userRole.toLowerCase();
        if (userRole == null || !userRole.equals("customer") || !userRole.equals("admin")){
            System.out.printf("Invalid userRole of user %s, auto set is customer", userId);
            this.userRole="customer";
        } else this.userRole=userRole;
    }
  
//Default constructor

public User() {
    this.userRole="customer";
}

// tự thêm constructor này vô vì class admin kê thừa cần viết super cóntructor
//----------------------------------------------------------------------------
public User(String userId, String userName, String userPassword,
 String userRegisterTime) {

    if (!userId.matches("^u_\\d{10}$")) {
            throw new IllegalArgumentException("Invalid userId format. Expected: u_ followed by 10 digits.");
        }
        this.userId = userId;
        this.userName= userName;
        this.userPassword= userPassword;
        
        // Validate date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        try {
            LocalDateTime.parse(userRegisterTime, formatter); // Just to validate format
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid userRegisterTime format. Expected format: DD-MM-YYYY_HH:MM:SS");
        }
         this.userRegisterTime = userRegisterTime;

}

    //toString
    @Override
    public String toString() {
        return "{"
            + "\"user_id\": \"" + userId + "\", "
            + "\"user_name\": \"" + userName + "\", "
            + "\"user_password\": \"" + userPassword + "\", "
            + "\"user_register_time\": \"" + userRegisterTime + "\", "
            + "\"user_role\": \"" + userRole + "\""
            + "}";
        }

}
