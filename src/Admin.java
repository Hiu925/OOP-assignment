public class Admin extends User {
    
   public Admin(String userId, String userName, String userPassword,String userRegisterTime, String userRole) {
   super(userId,userName,userPassword,userRegisterTime);
   if (userRole == null || userRole.isEmpty() || !userRole.equals("admin")){
       System.out.printf("Invalid userRole for admin with userid %s, auto set default into admin",userId);
       super.setUserRole("admin");
   }
   setUserRole("admin");
}

   //Default constructor
   public Admin() {
      super("u_0000000000", "","", "01-01-2000_00:00:00","admin");
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