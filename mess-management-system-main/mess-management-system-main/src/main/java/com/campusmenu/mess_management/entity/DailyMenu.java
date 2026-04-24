package com.campusmenu.mess_management.entity;

import com.campusmenu.mess_management.enums.MealType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "daily_menu", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"menu_date", "meal_type"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class DailyMenu  extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "menu_date", nullable = false)
    private LocalDate menuDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "items", nullable = false, columnDefinition = "json")
    private List<String> items;
    // Example: ["Poha", "Bread Butter", "Tea/Coffee"]

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_vendor_id", nullable = false)
    private Vendor vendor;

    // Business logic
    public boolean isToday() {
        return menuDate.equals(LocalDate.now());
    }

    public boolean isFuture() {
        return menuDate.isAfter(LocalDate.now());
    }

    public boolean isPast() {
        return menuDate.isBefore(LocalDate.now());
    }
}
