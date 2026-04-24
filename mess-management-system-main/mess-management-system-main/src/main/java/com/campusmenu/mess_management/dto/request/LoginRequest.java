package com.campusmenu.mess_management.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Login Request DTO
 * WITH EXPLICIT GETTERS (in case Lombok isn't working)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    // EXPLICIT GETTERS (backup if Lombok fails)
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // EXPLICIT SETTERS
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
