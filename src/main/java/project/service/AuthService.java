package project.service;

public interface AuthService <T> {
    T forgotPassword(String email);
    boolean resetPassword(String email, String password , String token);
    boolean changePassword(String oldPassword, String newPassword);
}
