package com.campusmenu.mess_management.dto.response;

import com.campusmenu.mess_management.enums.PlanType;
import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MealPlanResponse {
    private Long planId;
    private String planName;
    private PlanType planType;
    private Integer durationDays;
    private BigDecimal price;
    private BigDecimal discountPercentage;
    private BigDecimal finalPrice;
    private Map<String, Boolean> mealsIncluded;
    private String description;
    private Boolean isActive;
}
