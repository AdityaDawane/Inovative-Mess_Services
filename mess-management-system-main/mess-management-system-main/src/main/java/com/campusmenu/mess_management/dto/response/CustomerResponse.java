package com.campusmenu.mess_management.dto.response;
import lombok.*;
import java.time.LocalDateTime;

// No password field — never expose password in API response
@Data @Builder @NoArgsConstructor @AllArgsConstructor

public class CustomerResponse {
    private Long customerId;
    private String fullName;
    private String email;
    private String phone;
    private String profileImage;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
