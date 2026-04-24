package com.campusmenu.mess_management.entity;

import com.campusmenu.mess_management.enums.MealType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"customer_id", "meal_type", "scan_date"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(name = "scan_date", nullable = false)
    private LocalDate scanDate;

    @Column(name = "scan_time", nullable = false)
    private LocalDateTime scanTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scanned_by_vendor_id", nullable = false)
    private Vendor scannedByVendor;

    @Column(name = "machine_id", length = 50)
    private String machineId;

    @Column(name = "is_valid")
    private Boolean isValid = true;

    // Business logic
    public boolean isToday() {
        return scanDate.equals(LocalDate.now());
    }

    public boolean isSameDay(LocalDate date) {
        return scanDate.equals(date);
    }
}
