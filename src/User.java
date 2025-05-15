import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public abstract class User {
    protected String userId;
    protected String userName;
    protected String userPassword;
    protected String userRegisterTime;
    protected String userRole;

    public User (String userId, String userName,String userPassword, String userRegistrerTime, String userRole){
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

          // Assign default role if null or blank
        this.userRole = (userRole == null || userRole.isBlank()) ? "customer" : userRole;
    }
  
//Default constructor
// ko bt may bien con lai thi default type la gi?
public User() {
 // Implementation with default values
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


@overrride
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