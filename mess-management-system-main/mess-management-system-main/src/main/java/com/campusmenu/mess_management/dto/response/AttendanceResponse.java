package com.campusmenu.mess_management.dto.response;

import com.campusmenu.mess_management.enums.MealType;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor

public class AttendanceResponse {

    private Long attendanceId;
    private Long customerId;
    private String customerName;
    private MealType mealType;
    private LocalDate scanDate;
    private LocalDateTime scanTime;
    private String vendorName;
    private String messName;
    private Boolean isValid;
    private String message;
}
