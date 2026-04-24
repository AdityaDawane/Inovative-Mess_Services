package com.campusmenu.mess_management.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailyMenuRequest {
    private String mealType;
    private String items;
    private String description;
    private LocalDate menuDate;
    private Boolean isAvailable;
    private Long createdByVendorId;
}