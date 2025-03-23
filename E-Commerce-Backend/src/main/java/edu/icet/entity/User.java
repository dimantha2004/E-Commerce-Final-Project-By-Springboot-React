package edu.icet.entity;

import edu.icet.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

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

    private UserRole role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER) // Add this
    private Address address;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItemList;

    @Column(name = "created_at")
    private LocalDateTime createdAt=LocalDateTime.now();

    @Column(name = "otp")
    private String otp;

    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String email;
        private String password;
        private String phoneNumber;
        private UserRole role;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }

        public User build() {
            User user = new User();
            user.setName(this.name);
            user.setEmail(this.email);
            user.setPassword(this.password);
            user.setPhoneNumber(this.phoneNumber);
            user.setRole(this.role);
            return user;
        }
    }
}








