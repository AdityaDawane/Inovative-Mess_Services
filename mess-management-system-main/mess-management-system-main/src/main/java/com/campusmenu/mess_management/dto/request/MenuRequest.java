package com.campusmenu.mess_management.dto.request;
import com.campusmenu.mess_management.enums.MealType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor

public class MenuRequest {
    @NotNull(message = "Menu date is required")
    private LocalDate menuDate;

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotEmpty(message = "At least one menu item is required")
    private List<String> items;

    private String description;

    private Boolean isAvailable = true;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;
}
