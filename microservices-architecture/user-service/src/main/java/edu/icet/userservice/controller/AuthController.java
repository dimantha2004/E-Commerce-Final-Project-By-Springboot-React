package edu.icet.userservice.controller;

import edu.icet.userservice.dto.LoginRequest;
import edu.icet.userservice.dto.Response;
import edu.icet.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest) {
        Response response = userService.loginUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password/initiate")
    public ResponseEntity<Response> initiatePasswordReset(@RequestParam String email) {
        Response response = userService.initiatePasswordReset(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password/verify-otp")
    public ResponseEntity<Response> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        Response response = userService.verifyOtp(email, otp);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password/reset")
    public ResponseEntity<Response> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        Response response = userService.resetPassword(email, newPassword);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google")
    public ResponseEntity<Response> googleLogin(@RequestBody String credential) {
        Response response = userService.handleGoogleLogin(credential);
        return ResponseEntity.ok(response);
    }
}
