package edu.icet.userservice.controller;

import edu.icet.userservice.dto.Response;
import edu.icet.userservice.dto.UserDto;
import edu.icet.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody UserDto registrationRequest) {
        Response response = userService.registerUser(registrationRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<Response> getAllUsers() {
        Response response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-profile-info")
    public ResponseEntity<Response> getProfileInfo() {
        Response response = userService.getUserInfoAndOrderHistory();
        return ResponseEntity.ok(response);
    }
}
