public class Admin extends User {
    
   public Admin(String userId, String userName, String userPassword,String userRegisterTime, String userRole) {
   super(userId,userName,userPassword,userRegisterTime);
   if (userRole == null || userRole.isEmpty() || !userRole.equals("admin")){
       System.out.printf("Invalid userRole of user %s, auto set default into admin",userId);
       super.setUserRole("admin");
   }
   super.setUserRole("admin");
}

   //Default constructor
   public Admin() {
   // Implementation with default values
   }

   @Override
   public String toString() {
   return "{" 
        + "\"user_id\": \"" + getUserId() + "\", "
        + "\"user_name\": \"" + getUserName() + "\", "
        + "\"user_password\": \"" + getUserPassword() + "\", "
        + "\"user_register_time\": \"" + getUserRegisterTime() + "\", "
        + "\"user_role\": \"" + getUserRole() + "\""
        + "}";
   }


}
