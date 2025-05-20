package Model;

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
        this.userRegisterTime=userRegistrerTime;

        //Check userID
        if (!userId.matches("^u_\\d{10}$")) {
            System.out.println("Invalid userID, Expected format: u_{10 digits}. So auto set is u_0000000000");
            this.userId="u_0000000000";
        } else this.userId = userId;
        
        // // Validate date-time format
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        // try {
        //     // Thử parse để kiểm tra định dạng
        //     LocalDateTime.parse(userRegisterTime, formatter);
        //     this.userRegisterTime = userRegisterTime;
        // } catch (DateTimeParseException e) {
        //     // Nếu sai định dạng thì gán mặc định
        //     System.out.printf("Invalid time register of user %s, auto set to default 01-01-2000_00:00:00", userId);
        //     this.userRegisterTime = "01-01-2000_00:00:00";
        // }

        // Check userRole
        userRole=userRole.toLowerCase();
        if (userRole == null || !userRole.equals("customer") || !userRole.equals("admin")){
            System.out.printf("Invalid userRole of user %s, auto set is customer", userId);
            this.userRole="customer";
        } else this.userRole=userRole;
    }
  
//Default constructor

public User() {
    this.userId="u_0000000000";
    this.userName="";
    this.userPassword="";
    this.userRegisterTime="01-01-2000_00:00:00";
    this.userRole="customer";

}

// tự thêm constructor này vô vì class admin kê thừa cần viết super cóntructor
//----------------------------------------------------------------------------
public User(String userId, String userName, String userPassword,
 String userRegisterTime) {

        this.userId = userId;
        this.userName= userName;
        this.userPassword= userPassword;
        
        // Validate date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        try {
            // Parse to check format
            LocalDateTime.parse(userRegisterTime, formatter);
            this.userRegisterTime = userRegisterTime;
        } catch (DateTimeParseException e) {
            // If wrong format, set default
            System.out.printf("Invalid time register of user %s, auto set to default 01-01-2000_00:00:00\n", userId);
            this.userRegisterTime = "01-01-2000_00:00:00";
        }

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
    public String getUserId() {
        return userId;
    }
    public String getUserName() {
        return userName;
    }
    public String getUserPassword() {
        return userPassword;
    }
    public String getUserRegisterTime() {
        return userRegisterTime;
    }
    public String getUserRole() {
        return userRole;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
    public void setUserRegisterTime(String userRegisterTime) {
        this.userRegisterTime = userRegisterTime;
    }
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    //Check ValidUsername
    public static boolean validateUsername(String userName) {
    if (userName == null || userName.length() < 5) {
        return false;
    }

    // Check for valid characters using regex
    //''+'': one or more of the allowed characters
    return userName.matches("[a-zA-Z_]+");
    }

    //Check ValidPassword
    public static boolean validatePassword(String userPassword) {
    if (userPassword == null || userPassword.length() < 5) {
        return false;
    }

    // Regex: must contain at least one letter and one digit
    boolean hasLetter = userPassword.matches(".*[a-zA-Z].*");  //.* *. {check ở vị trí bất kì}
    boolean hasDigit = userPassword.matches(".*[0-9].*");

    return hasLetter && hasDigit;
    }

}