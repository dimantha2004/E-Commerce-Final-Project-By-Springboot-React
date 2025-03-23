package edu.icet.controller;


import edu.icet.dto.LoginRequest;
import edu.icet.dto.Response;
import edu.icet.dto.UserDto;
import edu.icet.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody UserDto registrationRequest){
        System.out.println(registrationRequest);
        return ResponseEntity.ok(userService.registerUser(registrationRequest));
    }
    @PostMapping("/login")
    public ResponseEntity<Response> loginUser(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<Response> forgotPassword(@RequestParam String email) {
        return ResponseEntity.ok(userService.initiatePasswordReset(email));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Response> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        return ResponseEntity.ok(userService.verifyOtp(email, otp));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response> resetPassword(@RequestParam String email,
                                                  @RequestParam String newPassword) {
        return ResponseEntity.ok(userService.resetPassword(email, newPassword));
    }
    @PostMapping("/google")
    public ResponseEntity<Response> googleLogin(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        return ResponseEntity.ok(userService.handleGoogleLogin(token));
    }
}
