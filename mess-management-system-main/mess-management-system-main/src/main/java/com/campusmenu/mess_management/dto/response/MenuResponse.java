package com.campusmenu.mess_management.dto.response;
import com.campusmenu.mess_management.enums.MealType;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MenuResponse {
    private Long menuId;
    private LocalDate menuDate;
    private MealType mealType;
    private List<String> items;
    private String description;
    private Boolean isAvailable;
    private String vendorName;
    private String messName;
}
