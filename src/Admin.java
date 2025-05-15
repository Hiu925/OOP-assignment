public class Admin extends User {
    
   public Admin(String userId, String userName, String userPassword,
 String userRegisterTime, String userRole) {
    
    super(userId,userName,userPassword,userRegisterTime);
 
     super.userRole = (userRole == null || userRole.isBlank()) ? "admin" : userRole;

   }

/**
* Default constructor
*/
public Admin() {
 // Implementation with default values
   }

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
