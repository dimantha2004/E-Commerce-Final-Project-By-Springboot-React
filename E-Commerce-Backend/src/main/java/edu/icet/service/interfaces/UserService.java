package edu.icet.service.interfaces;

import edu.icet.dto.LoginRequest;
import edu.icet.dto.Response;
import edu.icet.dto.UserDto;
import edu.icet.entity.User;

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
