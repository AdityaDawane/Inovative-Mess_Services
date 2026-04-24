package com.campusmenu.mess_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Vendor extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_id")
    private Long vendorId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "mess_name", nullable = false, length = 100)
    private String messName;

    @Column(name = "machine_id", unique = true, length = 50)
    private String machineId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Relationships
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailyMenu> menus = new ArrayList<>();

    @OneToMany(mappedBy = "scannedByVendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> scannedAttendance = new ArrayList<>();

    // Helper methods
    public void addMenu(DailyMenu menu) {
        menus.add(menu);
        menu.setVendor(this);
    }

    public void addAttendance(Attendance attendance) {
        scannedAttendance.add(attendance);
        attendance.setScannedByVendor(this);
    }
}
