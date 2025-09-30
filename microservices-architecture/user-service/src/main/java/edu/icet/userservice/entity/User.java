package edu.icet.userservice.entity;

import edu.icet.userservice.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Please Enter Your Name...!")
    private String name;

    @Column(unique = true)
    @NotBlank(message = "Please Enter Your Email...!")
    private String email;

    @NotBlank(message = "Please Enter Your Password...!")
    private String password;

    @Column(name = "phone_number")
    @NotBlank(message = "Please Enter Your Phone Number...!")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Address address;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "otp")
    private String otp;

    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry;
}
