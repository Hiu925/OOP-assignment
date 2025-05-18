
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class UserOperation {

    private static final Set<String> generatedIds = new HashSet<>();
    private static final Random random = new Random();

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
    if (userName == null || userPassword == null) {
        return null;
    }

    // Example: Assuming a Map<String, User> userMap with username keys
    User user = userMap.get(userName);
    if (user == null) {
        return null;  // user not found
    }

    // Assuming User class has a method getEncryptedPassword()
    String storedEncryptedPassword = encryptPassword(user.getUserPassword());

    // Encrypt the provided password to compare
    String encryptedInputPassword = encryptPassword(userPassword);

    if (storedEncryptedPassword.equals(encryptedInputPassword)) {
        return user; // successful login
    }

    return null; // wrong password
  }
    
}
