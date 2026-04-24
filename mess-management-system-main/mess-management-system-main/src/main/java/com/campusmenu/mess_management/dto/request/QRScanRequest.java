package com.campusmenu.mess_management.dto.request;

import com.campusmenu.mess_management.enums.MealType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QRScanRequest {
    @NotBlank(message = "QR code is required")
    private String qrCode;

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    private String machineId;
}
