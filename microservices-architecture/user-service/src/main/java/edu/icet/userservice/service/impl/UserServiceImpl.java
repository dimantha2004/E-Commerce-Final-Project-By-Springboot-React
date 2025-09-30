package edu.icet.userservice.service.impl;

import edu.icet.userservice.dto.LoginRequest;
import edu.icet.userservice.dto.Response;
import edu.icet.userservice.dto.UserDto;
import edu.icet.userservice.entity.User;
import edu.icet.userservice.enums.UserRole;
import edu.icet.userservice.repository.UserRepository;
import edu.icet.userservice.security.JwtUtils;
import edu.icet.userservice.security.GoogleTokenVerifier;
import edu.icet.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final GoogleTokenVerifier googleTokenVerifier;

    @Override
    public Response registerUser(UserDto registrationRequest) {
        try {
            if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
                return Response.builder()
                        .status(400)
                        .message("User already exists with email: " + registrationRequest.getEmail())
                        .build();
            }

            User user = User.builder()
                    .name(registrationRequest.getName())
                    .email(registrationRequest.getEmail())
                    .password(passwordEncoder.encode(registrationRequest.getPassword()))
                    .phoneNumber(registrationRequest.getPhoneNumber())
                    .role(registrationRequest.getRole() != null ? registrationRequest.getRole() : UserRole.USER)
                    .createdAt(LocalDateTime.now())
                    .build();

            User savedUser = userRepository.save(user);
            UserDto userDto = mapUserToDto(savedUser);

            return Response.builder()
                    .status(200)
                    .message("User registered successfully")
                    .user(userDto)
                    .build();

        } catch (Exception e) {
            log.error("Error occurred during user registration: ", e);
            return Response.builder()
                    .status(500)
                    .message("Internal server error during registration")
                    .build();
        }
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtils.generateToken(user);
            UserDto userDto = mapUserToDto(user);

            return Response.builder()
                    .status(200)
                    .message("Login successful")
                    .token(token)
                    .role(user.getRole().name())
                    .expirationTime("6 months")
                    .user(userDto)
                    .build();

        } catch (Exception e) {
            log.error("Error occurred during login: ", e);
            return Response.builder()
                    .status(401)
                    .message("Invalid email or password")
                    .build();
        }
    }

    @Override
    public Response getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            List<UserDto> userDtos = users.stream()
                    .map(this::mapUserToDto)
                    .collect(Collectors.toList());

            return Response.builder()
                    .status(200)
                    .message("Users retrieved successfully")
                    .userList(userDtos)
                    .build();

        } catch (Exception e) {
            log.error("Error occurred while fetching users: ", e);
            return Response.builder()
                    .status(500)
                    .message("Internal server error")
                    .build();
        }
    }

    @Override
    public User getLoginUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Response getUserInfoAndOrderHistory() {
        try {
            User user = getLoginUser();
            UserDto userDto = mapUserToDto(user);

            return Response.builder()
                    .status(200)
                    .message("User info retrieved successfully")
                    .user(userDto)
                    .build();

        } catch (Exception e) {
            log.error("Error occurred while fetching user info: ", e);
            return Response.builder()
                    .status(500)
                    .message("Internal server error")
                    .build();
        }
    }

    @Override
    public Response initiatePasswordReset(String email) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                return Response.builder()
                        .status(404)
                        .message("User not found with email: " + email)
                        .build();
            }

            User user = userOptional.get();
            String otp = generateOtp();
            user.setOtp(otp);
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(15)); // OTP valid for 15 minutes
            userRepository.save(user);

            // TODO: Send OTP via email service
            log.info("OTP generated for user {}: {}", email, otp);

            return Response.builder()
                    .status(200)
                    .message("OTP sent to your email")
                    .build();

        } catch (Exception e) {
            log.error("Error occurred during password reset initiation: ", e);
            return Response.builder()
                    .status(500)
                    .message("Internal server error")
                    .build();
        }
    }

    @Override
    public Response verifyOtp(String email, String otp) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                return Response.builder()
                        .status(404)
                        .message("User not found")
                        .build();
            }

            User user = userOptional.get();
            if (user.getOtp() == null || !user.getOtp().equals(otp)) {
                return Response.builder()
                        .status(400)
                        .message("Invalid OTP")
                        .build();
            }

            if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
                return Response.builder()
                        .status(400)
                        .message("OTP has expired")
                        .build();
            }

            return Response.builder()
                    .status(200)
                    .message("OTP verified successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error occurred during OTP verification: ", e);
            return Response.builder()
                    .status(500)
                    .message("Internal server error")
                    .build();
        }
    }

    @Override
    public Response resetPassword(String email, String newPassword) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                return Response.builder()
                        .status(404)
                        .message("User not found")
                        .build();
            }

            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setOtp(null); // Clear OTP after successful password reset
            user.setOtpExpiry(null);
            userRepository.save(user);

            return Response.builder()
                    .status(200)
                    .message("Password reset successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error occurred during password reset: ", e);
            return Response.builder()
                    .status(500)
                    .message("Internal server error")
                    .build();
        }
    }

    @Override
    public Response handleGoogleLogin(String credential) {
        try {
            var payload = googleTokenVerifier.verify(credential);
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            Optional<User> userOptional = userRepository.findByEmail(email);
            User user;

            if (userOptional.isEmpty()) {
                // Register new user
                user = User.builder()
                        .name(name)
                        .email(email)
                        .password(passwordEncoder.encode("google_oauth_user")) // Placeholder password
                        .phoneNumber("N/A") // Google login might not provide phone
                        .role(UserRole.USER)
                        .createdAt(LocalDateTime.now())
                        .build();
                user = userRepository.save(user);
            } else {
                user = userOptional.get();
            }

            String token = jwtUtils.generateToken(user);
            UserDto userDto = mapUserToDto(user);

            return Response.builder()
                    .status(200)
                    .message("Google login successful")
                    .token(token)
                    .role(user.getRole().name())
                    .expirationTime("6 months")
                    .user(userDto)
                    .build();

        } catch (Exception e) {
            log.error("Error occurred during Google login: ", e);
            return Response.builder()
                    .status(401)
                    .message("Google authentication failed")
                    .build();
        }
    }

    private UserDto mapUserToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                null, // Don't expose password
                user.getPhoneNumber(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
