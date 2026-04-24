package com.campusmenu.mess_management.dto.response;
import com.campusmenu.mess_management.enums.PaymentMethod;
import com.campusmenu.mess_management.enums.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    private Long subscriptionId;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private String gatewayPaymentId;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String message;
}
