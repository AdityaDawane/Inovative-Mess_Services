package com.campusmenu.mess_management.entity;

import com.campusmenu.mess_management.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subscriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long subscriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private MealPlan mealPlan;

    // ✅ NEW: Link to specific vendor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    // ✅ UPDATED: Make nullable (generated after approval)
    @Column(name = "qr_code", unique = true)
    private String qrCode;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.PENDING;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // ✅ NEW: Payment tracking
    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "payment_order_id")
    private String paymentOrderId;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    // ✅ NEW: Approval tracking
    @Column(name = "is_approved")
    private Boolean isApproved = false;

    @Column(name = "approved_by_vendor_id")
    private Long approvedByVendorId;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // Relationships
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendanceRecords = new ArrayList<>();

    // Helper methods
    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setSubscription(this);
    }

    public void addAttendance(Attendance attendance) {
        attendanceRecords.add(attendance);
        attendance.setSubscription(this);
    }

    // Business logic
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE &&
                LocalDate.now().isAfter(startDate.minusDays(1)) &&
                LocalDate.now().isBefore(endDate.plusDays(1));
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate);
    }

    public long getDaysRemaining() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    // ✅ NEW: Check if pending approval
    public boolean isPending() {
        return status == SubscriptionStatus.PENDING && !isApproved;
    }
}