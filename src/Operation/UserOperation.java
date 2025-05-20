package Operation;
import Model.*;

import java.io.*;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

import Helper_Classes.CustomerListResult;

public class UserOperation {

    private static final Set<String> generatedIds = new HashSet<>();
    private static final Random random = new Random();
    private static final int PAGE_SIZE = 10;

    private static UserOperation instance;

    // ko bt de lam gi 
    private UserOperation(){

    }

    //Singleton
    public static UserOperation getInstance() {
        if (instance == null) {
            instance = new UserOperation();
        }
        return instance;
    }

   public static String generateUniqueUserId() {
        String userId;
        do {
            long number = 1000000000L + (long)(random.nextDouble() * 9000000000L); // ensures 10-digit number
            userId = "u_" + number;
        } while (generatedIds.contains(userId)); // ensure uniqueness during runtime

        generatedIds.add(userId);
        return userId;
    }
    
    // encrypted password
    public static String encryptPassword(String userPassword) {
        
        if (userPassword == null || userPassword.isEmpty()) {
            return "^^$$"; // minimal format for empty password
        }

        int pwdLength = userPassword.length();
        int randLength = pwdLength * 2;
        String randomString = generateRandomAlphaNumeric(randLength);

        StringBuilder encrypted = new StringBuilder("^^");// dùng stringbuider đỡ tốn dữ liệu

        for (int i = 0, j = 0; i < pwdLength; i++, j += 2) {
            encrypted.append(randomString.charAt(j));
            encrypted.append(randomString.charAt(j + 1)); // 2 letter in random string
            encrypted.append(userPassword.charAt(i)); // chosse 1 letter in passsword
        }

        encrypted.append("$$");
        return encrypted.toString(); // trả về kiểu string cho hợp kiểu
    }

    // "helper method" dung de ho tro cho encryptPassword method  (line 35)
    private static String generateRandomAlphaNumeric(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // tong bang chu cai vt thuong, vt hoa, so 0-9
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // decode encryted password
    public String decryptPassword(String encryptedPassword) {
    if (encryptedPassword == null || encryptedPassword.length() < 4) {
        return "";  // Invalid or empty encrypted password, at least (^^ and $$) —  4 characters
    }
    
    // Remove the prefix ^^ and suffix $$ extract the string from 2 to lenghth-2
    String core = encryptedPassword.substring(2, encryptedPassword.length() - 2);

    StringBuilder originalPwd = new StringBuilder();

    //Filter password
    // Every 3 chars: 2 random + 1 real password char=> increasement =3 each char in password
    for (int i = 2; i < core.length(); i += 3) {
        originalPwd.append(core.charAt(i));
    }

    return originalPwd.toString();
}


//-----------------------------------------------------------
// username collection as attribute
private Set<String> registeredUsernames = new HashSet<>();

public boolean checkUsernameExist(String userName) {
    if (userName == null || userName.isEmpty()) {
        return false;
    }
    return registeredUsernames.contains(userName);
}

// 2.6.6 check validation username
public boolean validateUsername(String userName) {
    if (userName == null || userName.length() < 5) {
        return false;
    }

    // Check for valid characters using regex
    //''+'': one or more of the allowed characters
    return userName.matches("[a-zA-Z_]+");
}

///2.6.7 validatePassword
public boolean validatePassword(String userPassword) {
    if (userPassword == null || userPassword.length() < 5) {
        return false;
    }

    // Regex: must contain at least one letter and one digit
    boolean hasLetter = userPassword.matches(".*[a-zA-Z].*");  //.* *. {check ở vị trí bất kì}
    boolean hasDigit = userPassword.matches(".*[0-9].*");

    return hasLetter && hasDigit;
}
//2.6.8 login

private final Map<String, User> userMap = new HashMap<>();

public void registerUser(User user) {
    if (user != null && user.getUserName() != null) {
        userMap.put(user.getUserName(), user);
        registeredUsernames.add(user.getUserName());
    }
}

//input username and userpassword
public User login(String userName, String userPassword) {
        User user = findUserByUsername(userName);
        if (user != null) {
            String encryptedPassword = user.getUserPassword(); // Lấy mật khẩu đã mã hóa từ file
            String decryptedPassword = decryptPassword(encryptedPassword); // Giải mã mật khẩu
            if (decryptedPassword.equals(userPassword)) {
                return user;
            }
        }
        return null;
    }

    //Read user from file
    public List<User> readUsersFromFile() {
        List<User> users = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("data/users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    User user = parseUserFromJson(line);
                    if (user != null) {
                        users.add(user);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
        }
        
        return users;
    }

    //adding base on role

    private User parseUserFromJson(String jsonLine) {
    try {
        // Print the exact JSON for debugging
        System.out.println("Attempting to parse: " + jsonLine);
        
        JSONObject json = new JSONObject(jsonLine);
        
        // Get required fields with validation
        String userId = json.getString("user_id");
        String userName = json.getString("user_name");
        String userPassword = json.getString("user_password");
        String userRegisterTime = json.getString("user_register_time");
        String userRole = json.getString("user_role");
        
        if ("admin".equals(userRole)) {
            return new Admin(userId, userName, userPassword, userRegisterTime, userRole);
        } else if ("customer".equals(userRole)) {
            // Kiểm tra xem các trường customer có tồn tại không
            String userEmail = json.optString("user_email", ""); // Trả về chuỗi rỗng nếu không tìm thấy
            String userMobile = json.optString("user_mobile", ""); // Trả về chuỗi rỗng nếu không tìm thấy
            
            return new Customer(userId, userName, userPassword, userRegisterTime, 
                               userRole, userEmail, userMobile);
        } else {
            System.err.println("Unknown user role: " + userRole);
        }
        
    } catch (JSONException e) {
        System.err.println("JSON parsing error: " + e.getMessage());
        e.printStackTrace();
    } catch (Exception e) {
        System.err.println("General error: " + e.getClass().getName() + " - " + e.getMessage());
        e.printStackTrace();
    }
    
    return null;
}

    public void saveUsersToFile(List<User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("data/users.txt"))) {
            for (User user : users) {
                writer.println(user.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving users file: " + e.getMessage());
        }
    }

    public User findUserByUsername(String userName) {
        List<User> users = readUsersFromFile();
        return users.stream()
                   .filter(user -> user.getUserName().equals(userName))
                   .findFirst()
                   .orElse(null);
    }

    public CustomerListResult getCustomerList(int pageNumber) {
    List<Customer> customers = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader("data/users.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JSONObject userJson = new JSONObject(line);
                // CHỈ lấy customer, bỏ qua admin
                if ("customer".equals(userJson.getString("user_role"))) {
                    Customer customer = convertJsonToCustomer(userJson);
                    customers.add(customer);
                }
            } catch (JSONException e) {
                // Bỏ qua dòng lỗi
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    // Xử lý phân trang
    int totalPages = (int) Math.ceil((double) customers.size() / PAGE_SIZE);
    int fromIndex = (pageNumber - 1) * PAGE_SIZE;
    int toIndex = Math.min(fromIndex + PAGE_SIZE, customers.size());
    
    return new CustomerListResult(
        customers.subList(fromIndex, toIndex),
        pageNumber,
        totalPages
    );
}

private Customer convertJsonToCustomer(JSONObject json) throws JSONException {
    // Validate required fields
    if (!json.has("user_id") || !json.has("user_name") || 
        !json.has("user_email") || !json.has("user_mobile")) {
        throw new JSONException("Missing required customer fields");
    }
    
    return new Customer(
        json.getString("user_id"),
        json.getString("user_name"),
        "", // Password không hiển thị
        json.optString("user_register_time", ""),
        "customer",
        json.getString("user_email"),
        json.getString("user_mobile")
    );
}

}