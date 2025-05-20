package Operation;
import Model.*;
import Helper_Classes.*;

import org.json.*;
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

   // 2.7.1 Singleton
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
   //2.7.3 validate MobileNum
   public boolean validateMobile(String userMobile) {
    if (userMobile == null) {
        return false;
    }

    String mobileRegex = "^(04|03)\\d{8}$";

    return userMobile.matches(mobileRegex);
}

//2.7.4. registerCustomer()

public boolean registerCustomer(String userName, String userPassword,String userEmail, String userMobile) {

    // Validate inputs (implement your own username and password validation)
    if (!User.validatePassword(userPassword)) System.out.println("Error Password");
    if (!User.validateUsername(userName)) System.out.println("Error Name");
    if (!validateEmail(userEmail)) System.out.println("Error email");
    if (!validateMobile(userMobile)) System.out.println("Error mobile");
    if (!User.validatePassword(userPassword) || !User.validateUsername(userName) || !validateEmail(userEmail) || !validateMobile(userMobile)) {
        return false;
    }


    String filePath = "data/users.txt";

    // Check if username already exists
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JSONObject jsonObject = new JSONObject(line);
                if (jsonObject.has("user_name") && 
                    jsonObject.getString("user_name").equalsIgnoreCase(userName)) {
                    // Username exists
                    return false;
                }
            } catch (JSONException e) {
                System.err.println("Error parsing JSON line: " + line);
                e.printStackTrace();
                // Continue to next line if this one has parsing issues
            }
        }
    } catch (FileNotFoundException e) {
        // File might not exist yet — treat as empty database
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
    System.out.println("Check name " + userName);
    // Generate unique user ID (e.g., UUID or a timestamp-based id)
    String userId = UserOperation.generateUniqueUserId();
    System.out.println("Check id " + userId);
    // Get current time as registration time
    String registerTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    System.out.println("Check date"); 
    // Create JSON object
    JSONObject customerJson = new JSONObject();
    try {
        customerJson.put("user_id", userId);
        customerJson.put("user_name", userName);
        customerJson.put("user_password", userPassword);
        customerJson.put("user_register_time", registerTime);
        customerJson.put("user_role", "customer");
        customerJson.put("user_email", userEmail);
        customerJson.put("user_mobile", userMobile);
    } catch (JSONException e) {
        e.printStackTrace();
        return false;
    }

    System.out.println("Check JSON: " + customerJson);

    // Append JSON data to file
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
        writer.write(customerJson.toString());
        writer.newLine();
        return true;
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}

 //2.7.5 updateProfile
public boolean updateProfile(String attributeName, String value, Customer customerObject) {
    if (attributeName == null || value == null || customerObject == null) {
        return false;
    }

    boolean updatedInMemory = false;

    // Cập nhật đối tượng trong bộ nhớ
    switch (attributeName.toLowerCase()) {
        case "user_name":
            if (User.validateUsername(value)) {
                customerObject.setUserName(value);
                updatedInMemory = true;
            }
            break;

        case "user_password":
            if (User.validatePassword(value)) {
                customerObject.setUserPassword(UserOperation.encryptPassword(value));
                updatedInMemory = true;
            }
            break;

        case "user_email":
            if (validateEmail(value)) {
                customerObject.setUserEmail(value);
                updatedInMemory = true;
            }
            break;

        case "user_mobile":
            if (validateMobile(value)) {
                customerObject.setUserMobile(value);
                updatedInMemory = true;
            }
            break;

        default:
            // Thuộc tính không xác định
            return false;
    }

    if (updatedInMemory) {
        return updateUserInFile(customerObject);
    }
    return false;
}

//create json from user data
private JSONObject createJsonFromUser(User userObject) throws JSONException {
    JSONObject jsonUser = new JSONObject();
    
    // Thêm các thuộc tính chung cho mọi loại người dùng
    jsonUser.put("user_id", userObject.getUserId());
    jsonUser.put("user_name", userObject.getUserName());
    jsonUser.put("user_password", userObject.getUserPassword());
    jsonUser.put("user_register_time", userObject.getUserRegisterTime());
    jsonUser.put("user_role", userObject.getUserRole());
    
    // Thêm các thuộc tính đặc thù cho khách hàng
    if (userObject instanceof Customer) {
        Customer customer = (Customer) userObject;
        jsonUser.put("user_email", customer.getUserEmail());
        jsonUser.put("user_mobile", customer.getUserMobile());
    }
    
    return jsonUser;
}

    //Cập nhật trong file
private boolean updateUserInFile(User userObject) {
    String filePath = "data/users.txt";
    List<String> fileContent = new ArrayList<>();

    boolean userFound = false; //Check
    
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JSONObject jsonUser = new JSONObject(line);
                // Tìm người dùng cần cập nhật dựa trên ID
                if (jsonUser.getString("user_id").equals(userObject.getUserId())) {
                    // Tạo JSON mới với thông tin đã cập nhật
                    JSONObject updatedJson = createJsonFromUser(userObject);
                    fileContent.add(updatedJson.toString());
                    userFound = true;
                } else {
                    // Giữ nguyên thông tin các người dùng khác
                    fileContent.add(line);
                }
            } catch (JSONException e) {
                // Nếu có lỗi parsing, giữ lại dòng gốc
                fileContent.add(line);
                System.err.println("Error parsing JSON: " + e.getMessage());
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading user file: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
    
    // Nếu không tìm thấy người dùng, trả về false
    if (!userFound) {
        return false;
    }
    
    // Ghi lại toàn bộ file với thông tin đã cập nhật
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        for (String jsonLine : fileContent) {
            writer.write(jsonLine);
            writer.newLine();
        }
        return true;
    } catch (IOException e) {
        System.err.println("Error writing to user file: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
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



