package edu.icet.userservice.service;

import edu.icet.userservice.dto.LoginRequest;
import edu.icet.userservice.dto.Response;
import edu.icet.userservice.dto.UserDto;
import edu.icet.userservice.entity.User;

public interface UserService {
    Response registerUser(UserDto registrationRequest);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUsers();
    User getLoginUser();
    Response getUserInfoAndOrderHistory();
    Response initiatePasswordReset(String email);
    Response verifyOtp(String email, String otp);
    Response resetPassword(String email, String newPassword);
    Response handleGoogleLogin(String credential);
}
