package edu.icet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {
    @NotBlank(message = "Please Enter Your Email...!")
    private String email;

    @NotBlank(message = "Please Enter Your Password...!")
    private String password;
}
