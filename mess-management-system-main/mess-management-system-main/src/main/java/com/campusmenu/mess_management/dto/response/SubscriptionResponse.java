package com.campusmenu.mess_management.dto.response;
import com.campusmenu.mess_management.enums.SubscriptionStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor

public class SubscriptionResponse {
    private Long subscriptionId;
    private Long customerId;
    private String customerName;
    private Long planId;
    private String planName;
    private String qrCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;
    private BigDecimal totalAmount;
    private Long daysRemaining;
}
