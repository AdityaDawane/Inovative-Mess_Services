package com.campusmenu.mess_management.dto.request;


import lombok.Data;

@Data
public class MealPlanRequest {
    private String planName;
    private String planType;
    private Integer durationDays;
    private Double price;
    private Double discountPercentage;
    private Double finalPrice;
    private String mealsIncluded;
    private String description;
    private Boolean isActive;
}
