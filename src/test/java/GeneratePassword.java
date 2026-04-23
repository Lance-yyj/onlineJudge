import cn.hutool.crypto.digest.BCrypt;

public class GeneratePassword {
    public static void main(String[] args) {
        String password = "admin123";
        String hashed = BCrypt.hashpw(password);
        System.out.println("Original password: " + password);
        System.out.println("BCrypt hash: " + hashed);
        System.out.println("");
        System.out.println("Verification: " + BCrypt.checkpw(password, hashed));
    }
}
