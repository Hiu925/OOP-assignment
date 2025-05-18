import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerOperation {
    
    private static CustomerOperation instance;

    // ko bt dung lam gi
    private CustomerOperation() {
        
    }

   // 2.7.1 get instance
    public static synchronized CustomerOperation getInstance() {
        if (instance == null) {
            instance = new CustomerOperation();
        }
        return instance;
    }

    //2.7.2 validate email
    public boolean validateEmail(String userEmail) {
    if (userEmail == null) {
        return false;
    }
    
    // Simple regex pattern for email validation
    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    
    Pattern pattern = Pattern.compile(emailRegex);
    Matcher matcher = pattern.matcher(userEmail);
    
    return matcher.matches();
}
   //2.7.3 validate Mobile
   public boolean validateMobile(String userMobile) {
    if (userMobile == null) {
        return false;
    }

    String mobileRegex = "^(04|03)\\d{8}$";

    return userMobile.matches(mobileRegex);
}

//2.7.4. registerCustomer()

public boolean registerCustomer(String userName, String userPassword,
                                String userEmail, String userMobile) {
    // Validate inputs (implement your own username and password validation)
    if (userName == null || userName.isEmpty() || userPassword == null || userPassword.isEmpty()
        || !validateEmail(userEmail) || !validateMobile(userMobile)) {
        return false;
    }

    String filePath = "data/users.txt";

    // Check if username already exists
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            // Assuming CSV format: userId,userName,userPassword,userEmail,userMobile,registerTime
            String[] parts = line.split(",");
            if (parts.length > 1) {
                String existingUserName = parts[1];
                if (existingUserName.equalsIgnoreCase(userName)) {
                    // Username exists
                    return false;
                }
            }
        }
    } catch (FileNotFoundException e) {
        // File might not exist yet — treat as empty database
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }

    // Generate unique user ID (e.g., UUID or a timestamp-based id)
    String userId = generateUniqueUserId();

    // Get current time as registration time
    String registerTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    // Construct data line to save
    String customerData = userId + "," + userName + "," + userPassword + "," + userEmail + "," + userMobile + "," + registerTime;

    // Append data to file
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
        writer.write(customerData);
        writer.newLine();
        return true;
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}
// Helper method to generate unique user ID
private String generateUniqueUserId() {
    // Simple example: use current timestamp in milliseconds
    return "U" + System.currentTimeMillis();
 }


 //2.7.5 updateProfile
public boolean updateProfile(String attributeName, String value, Customer customerObject) {
    if (attributeName == null || value == null || customerObject == null) {
        return false;
    }

    switch (attributeName.toLowerCase()) {
        case "username":
            if (!value.isEmpty()) {
                customerObject.setUserName(value);
                return true;
            }
            break;

        case "password":
            if (!value.isEmpty()) {
                customerObject.setUserPassword(value);
                return true;
            }
            break;

        case "email":
            if (validateEmail(value)) {
                customerObject.setUserEmail(value);
                return true;
            }
            break;

        case "mobile":
            if (validateMobile(value)) {
                customerObject.setUserMobile(value);
                return true;
            }
            break;

        default:
            // Unknown attribute
            return false;
    }

    // If validation failed
    return false;
 }


 //2.7.6 delete Cútomer
 private static final String FILE_PATH = "data/users.txt";
 private static final String TEMP_FILE_PATH = "data/users_temp.txt";

 public boolean deleteCustomer(String customerId) {
        boolean deleted = false;

        try {
            File inputFile = new File(FILE_PATH);
            File tempFile = new File(TEMP_FILE_PATH);

            if (!inputFile.exists()) {
                return false;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

                String line;

                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0 && tokens[0].equals(customerId)) {
                        deleted = true; // Skip this line (delete it)
                        continue;
                    }
                    writer.write(line);
                    writer.newLine();
                }
            }

            // Replace original file with temp file
            if (deleted) {
                Files.delete(inputFile.toPath());
                Files.move(tempFile.toPath(), inputFile.toPath());
            } else {
                // If not deleted, remove the temp file
                Files.deleteIfExists(tempFile.toPath());
            }

            return deleted;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

//2.7.7 getcustomerlist
    private static final int PAGE_SIZE = 10;

    public CustomerListResult getCustomerList(int pageNumber) {
        List<Customer> allCustomers = new ArrayList<>();

        // Read customers from file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 6) {
                    Customer customer = new Customer(
                        tokens[0], tokens[1], tokens[2],
                        tokens[3], tokens[4], tokens[5]
                    );
                    allCustomers.add(customer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new CustomerListResult(Collections.emptyList(), pageNumber, 0);
        }

        // Calculate pagination
        int totalCustomers = allCustomers.size();
        int totalPages = (int) Math.ceil((double) totalCustomers / PAGE_SIZE);

        // Validate page number
        if (pageNumber < 1 || pageNumber > totalPages) {
            return new CustomerListResult(Collections.emptyList(), pageNumber, totalPages);
        }

        int startIndex = (pageNumber - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalCustomers);

        List<Customer> currentPageList = allCustomers.subList(startIndex, endIndex);

        return new CustomerListResult(currentPageList, pageNumber, totalPages);
    }
    
//2.7.8 delete customers
    public void deleteAllCustomers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            // Opening FileWriter in default (non-append) mode clears the file
            writer.write(""); // Optional: writes nothing (clears content)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}



