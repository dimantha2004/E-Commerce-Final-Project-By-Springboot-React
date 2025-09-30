package edu.icet.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private int status;
    private String message;
    private String token;
    private String role;
    private String expirationTime;
    private UserDto user;
    private List<UserDto> userList;
}
