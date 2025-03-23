package edu.icet.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import edu.icet.dto.LoginRequest;
import edu.icet.dto.Response;
import edu.icet.dto.UserDto;
import edu.icet.entity.User;
import edu.icet.enums.UserRole;
import edu.icet.exception.InvalidCredentialsException;
import edu.icet.exception.NotFoundException;
import edu.icet.mapper.EntityDtoMapper;
import edu.icet.repository.UserRepository;
import edu.icet.security.GoogleTokenVerifier;
import edu.icet.security.JwtUtils;
import edu.icet.service.interfaces.EmailService;
import edu.icet.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EntityDtoMapper entityDtoMapper;
    private final EmailService emailService;
    private final GoogleTokenVerifier googleTokenVerifier;

    @Override
    public Response registerUser(UserDto registrationRequest) {
        UserRole role = UserRole.USER;

        if (registrationRequest.getRole() != null && registrationRequest.getRole().equalsIgnoreCase("admin")) {
            role = UserRole.ADMIN;
        }

        User user = User.builder()
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .phoneNumber(registrationRequest.getPhoneNumber())
                .role(role)
                .build();

        User savedUser = userRepo.save(user);
        System.out.println(savedUser);

        UserDto userDto = entityDtoMapper.mapUserToDtoBasic(savedUser);
        return Response.builder()
                .status(200)
                .message("User Added Succesfully...!")
                .user(userDto)
                .build();
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {
        User user = userRepo.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("Email not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Password does not match");
        }

        String token = jwtUtils.generateToken(user);

        return Response.builder()
                .status(200)
                .message("User Successfully Logged In")
                .token(token)
                .expirationTime("6 Month")
                .role(user.getRole().name())
                .build();
    }

    @Override
    public Response getAllUsers() {
        List<User> users = userRepo.findAll();
        System.out.println(users);
        List<UserDto> userDtos = users.stream()
                .map(entityDtoMapper::mapUserToDtoBasic)
                .toList();

        return Response.builder()
                .status(200)
                .userList(userDtos)
                .build();
    }

    @Override
    public User getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info("User Email is: " + email);
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not found"));
    }

    @Override
    public Response getUserInfoAndOrderHistory() {
        User user = getLoginUser();
        UserDto userDto = entityDtoMapper.mapUserToDtoPlusAddressAndOrderHistory(user);

        return Response.builder()
                .status(200)
                .user(userDto)
                .build();
    }
    public Response initiatePasswordReset(String email) {
        try {
            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("Email not found"));

            String otp = generateOtp();
            user.setOtp(otp);
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
            userRepo.save(user);

            emailService.sendOtpEmail(user.getEmail(), otp);

            return Response.builder()
                    .status(200)
                    .message("OTP sent to email")
                    .build();
        } catch (Exception e) {
            log.error("Password reset failed for email: {}", email, e);
            return Response.builder()
                    .status(500)
                    .message("Failed to send OTP: " + e.getMessage())
                    .build();
        }
    }

    public Response verifyOtp(String email, String otp) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Email not found"));

        if (!user.getOtp().equals(otp) || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            throw new InvalidCredentialsException("Invalid or expired OTP");
        }

        return Response.builder()
                .status(200)
                .message("OTP verified")
                .build();
    }

    public Response resetPassword(String email, String newPassword) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Email not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepo.save(user);

        return Response.builder()
                .status(200)
                .message("Password reset successful")
                .build();
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    @Override
    public Response handleGoogleLogin(String credential) {
        try {
            GoogleIdToken.Payload payload = googleTokenVerifier.verify(credential);
            String email = payload.getEmail();

            User user = userRepo.findByEmail(email)
                    .orElseGet(() -> createGoogleUser(payload));

            String token = jwtUtils.generateToken(user);

            return Response.builder()
                    .status(200)
                    .token(token)
                    .role(user.getRole().name())
                    .message("Google login successful")
                    .build();

        } catch (Exception e) {
            // Log the specific exception
            System.err.println("Google login error: " + e.getMessage());
            e.printStackTrace();

            return Response.builder()
                    .status(400)
                    .message("Invalid Google token: " + e.getMessage())
                    .build();
        }
    }

    private User createGoogleUser(GoogleIdToken.Payload payload) {
        User newUser = new User();
        newUser.setEmail(payload.getEmail());
        newUser.setName((String) payload.get("name"));
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        newUser.setRole(UserRole.USER);
        newUser.setPhoneNumber("N/A"); // Set a default value
        return userRepo.save(newUser);
    }
}