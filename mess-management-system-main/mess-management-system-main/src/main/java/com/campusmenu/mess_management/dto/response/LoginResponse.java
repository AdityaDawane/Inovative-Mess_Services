package com.campusmenu.mess_management.dto.response;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor

public class LoginResponse {
    private String token;       // JWT token
    private String tokenType;   // "Bearer"
    private Long userId;
    private String fullName;
    private String email;
    private String role;        // CUSTOMER / VENDOR / ADMIN
    private String message;
}
