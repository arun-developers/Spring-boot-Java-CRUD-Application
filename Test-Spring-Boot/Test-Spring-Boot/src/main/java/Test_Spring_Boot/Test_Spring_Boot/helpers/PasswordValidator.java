package Test_Spring_Boot.Test_Spring_Boot.helpers;

public class PasswordValidator {

    public static boolean StrongPasswordValidator(String password) {

        if (password == null || password.isEmpty()) {
            return false;
        }

        // Minimum 8 characters, at least one uppercase, one lowercase, one digit, and one special character
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

        // Validate the password using regex
        return password.matches(passwordPattern);
    }
}
