public class Customer extends User {
    private String userEmail;
    private String userMobile;
    
    public Customer(String userId, String userName, String userPassword,
 String userRegisterTime, String userRole,
 String userEmail, String userMobile) {
 
    super( userId,userName,userPassword,userRegisterTime,userRole);
    this.userEmail=userEmail;
    this.userMobile=userMobile;
   
}
 // default constructor
   public Customer(){
         super();
   }
   @Override
   public String toString(){
      return "{"
        + "\"user_id\": \"" + userId + "\", "
        + "\"user_name\": \"" + userName + "\", "
        + "\"user_password\": \"" + userPassword + "\", "
        + "\"user_register_time\": \"" + userRegisterTime + "\", "
        + "\"user_role\": \"" + userRole + "\""
        + "\"user_email\": \" "+ userEmail+"@gmail.com \", "
        + "\"user_mobile\":\""+ userMobile
        + "}";

   }

}
