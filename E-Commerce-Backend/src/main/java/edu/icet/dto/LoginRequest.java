package edu.icet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

public class LoginRequest {
    @NotBlank(message = "Please Enter Your Email...!")
    private String email;

    @NotBlank(message = "Please Enter Your Password...!")
    private String password;
}
