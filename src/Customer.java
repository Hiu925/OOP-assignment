public class Customer extends User {
    private String userEmail;
    private String userMobile;
    
    public Customer(String userId, String userName, String userPassword,String userRegisterTime, String userRole,String userEmail, String userMobile) {
 
    super( userId,userName,userPassword,userRegisterTime,userRole);
    this.userMobile=userMobile;

    //Check email
    if (isValidEmail(userEmail)==true)
    this.userEmail=userEmail;
    else {
      System.out.println("Invalid email, auto set to default email example@gmail.com");
      this.userEmail="example@gmail.com";
    }
   
}
    // Default constructor
    public Customer(){
        super("u_0000000000", "", "", "01-01-2000_00:00:00","customer");
        this.userEmail="example@gmail.com";
        this.userMobile="";
    }
   //Getter and Setter
   public String getUserEmail() {
       return userEmail;
   }
   public String getUserMobile() {
       return userMobile;
   }
   public void setUserEmail(String userEmail) {
       this.userEmail = userEmail;
   }
   public void setUserMobile(String userMobile) {
       this.userMobile = userMobile;
   }

   private boolean isValidEmail(String email) {
        if (email == null) return false;
        // Regex để kiểm tra định dạng email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

   @Override
   public String toString(){
      return "{"
        + "\"user_id\": \"" + getUserId() + "\", "
        + "\"user_name\": \"" + getUserName() + "\", "
        + "\"user_password\": \"" + getUserPassword() + "\", "
        + "\"user_register_time\": \"" + getUserRegisterTime() + "\", "
        + "\"user_role\": \"" + getUserRole() + "\""
        + "\"user_email\": \" "+ userEmail+"@gmail.com \", "
        + "\"user_mobile\":\""+ userMobile
        + "}";

   }
   
   //----------------------------------------------------------------------
   // constructor hổ trợ hàm  (2.7.7) getCustomerList trong class customeroperation ở dòng 215 
   public Customer(String id, String username, String password, String email, String mobile, String registerTime) {
        
     super(id,username,password,registerTime);
       
        this.userEmail = email;
        this.userMobile = mobile;
        
    }

}