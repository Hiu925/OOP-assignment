package Operation;

import Model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdminOperation {
   
    private static final String ADMIN_FILE_PATH = "data/admins.txt";
    private final String DEFAULT_ADMIN_PASSWORD = "A123456";
    private final String DEFAULT_ADMIN_USERNAME = "admin";
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

    public void registerAdmin() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(ADMIN_FILE_PATH));
            for (String line : lines) {
                if (line.contains("\"user_role\":\"admin\"")) {
                    // Admin already exists, no need to register again
                    return;
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking for existing admin: " + e.getMessage());
        }

            //auto create admin account
            String userId = UserOperation.generateUniqueUserId();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
            String registerTime = sdf.format(new Date());
            Admin admin = new Admin(userId, DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD, registerTime, "admin");

            System.out.println("Admin account created successfully.");

        // Encrypt password for storage
        String encryptedPassword = UserOperation.encryptPassword(DEFAULT_ADMIN_PASSWORD);
        
        // Convert to string format for storage
        String adminString = admin.toString();
        // Replace the original password with encrypted password in the string
        adminString = adminString.replace("\"user_password\":\"" + DEFAULT_ADMIN_PASSWORD + "\"", 
                                         "\"user_password\":\"" + encryptedPassword + "\"");
        
    try {
            Files.write(Paths.get(ADMIN_FILE_PATH), 
                      (adminString + System.lineSeparator()).getBytes(), //ghi kí tự + xuống dòng phù hợp với hệ điều hành
                      StandardOpenOption.APPEND); //ghi cuối file
        } catch (IOException e) {
            System.err.println("Error registering admin: " + e.getMessage());
        }
    }
}
