import java.util.Scanner;

public class AdminOperation {
   
    private static AdminOperation instance;

    //private cóntructor
    private AdminOperation() {
        
    }
    public static AdminOperation getInstance() {
        if (instance == null) {
            instance = new AdminOperation();
        }
        return instance;
    }

//2.8.2 registerAdmin()

Scanner scanner = new Scanner(System.in);

    private Admin adminAccount; // thêm biến vô để lưu lại admin account

    private boolean adminRegistered = false;  
    public void registerAdmin() {
        if (!adminRegistered) 
        {
            //??? ko bt làm sao tạo admin acconut
            //====================================
            // tụ thêm vô tạo object là admin có thể sai đang nghi ngờ 
            System.out.println("Registering admin account...");

            System.out.println("input user ID: ");
            String userId=scanner.next();       

            scanner.nextLine();
            System.out.println("input user name: ");
            String userName=scanner.next();
                
            scanner.nextLine();
            System.out.println("input user password: ");
            String userPassword=scanner.next();
   
            scanner.nextLine();
            System.out.println("input registertime");
            String userRegisterTime=scanner.next();
            
            scanner.nextLine();
            System.out.println("input role");
            String userRole=scanner.next();
            
         adminAccount = new Admin( userId, userName, userPassword,userRegisterTime, userRole);
          //=====================================

            System.out.println("Admin account created successfully.");

            adminRegistered = true;  // Mark as registered
        } else {
            System.out.println("Admin account is already registered.");
        }
    }

   // thêm getter để lấy giá trị admin account tạo ra trong hàm regíter admin
    public Admin getAdminAccount() {
        return adminAccount;
    }

}
