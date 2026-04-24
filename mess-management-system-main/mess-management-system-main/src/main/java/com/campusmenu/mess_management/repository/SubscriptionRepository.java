package com.campusmenu.mess_management.repository;

import com.campusmenu.mess_management.entity.Subscription;
import com.campusmenu.mess_management.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // ========== EXISTING METHODS (Keep these) ==========

    Optional<Subscription> findByQrCode(String qrCode);

    List<Subscription> findByCustomer_CustomerId(Long customerId);

    List<Subscription> findByStatus(SubscriptionStatus status);

    @Query("SELECT s FROM Subscription s WHERE s.customer.customerId = :customerId " +
            "AND s.status = :status ORDER BY s.createdAt DESC")
    List<Subscription> findByCustomerIdAndStatus(
            @Param("customerId") Long customerId,
            @Param("status") SubscriptionStatus status
    );

    @Query("SELECT s FROM Subscription s WHERE s.customer.customerId = :customerId " +
            "AND s.status = 'ACTIVE' AND s.endDate >= CURRENT_DATE")
    List<Subscription> findActiveSubscriptionsByCustomer(@Param("customerId") Long customerId);

    @Query("SELECT s FROM Subscription s WHERE s.qrCode = :qrCode " +
            "AND s.status = 'ACTIVE' AND s.startDate <= :currentDate " +
            "AND s.endDate >= :currentDate")
    Optional<Subscription> findActiveSubscriptionByQrCode(
            @Param("qrCode") String qrCode,
            @Param("currentDate") LocalDate currentDate
    );

    @Query("SELECT s FROM Subscription s WHERE s.endDate < CURRENT_DATE " +
            "AND s.status = 'ACTIVE'")
    List<Subscription> findExpiredActiveSubscriptions();

    boolean existsByQrCode(String qrCode);

    // ========== NEW METHODS FOR VENDOR SELECTION ==========

    // ✅ Find all subscriptions for a vendor with specific status
    @Query("SELECT s FROM Subscription s WHERE s.vendor.vendorId = :vendorId " +
            "AND s.status = :status ORDER BY s.createdAt DESC")
    List<Subscription> findByVendorIdAndStatus(
            @Param("vendorId") Long vendorId,
            @Param("status") SubscriptionStatus status
    );

    // ✅ Find pending payments for a vendor (waiting for approval)
    @Query("SELECT s FROM Subscription s WHERE s.vendor.vendorId = :vendorId " +
            "AND s.status = :status AND s.isApproved = :isApproved " +
            "ORDER BY s.paymentDate DESC")
    List<Subscription> findByVendorIdAndStatusAndIsApproved(
            @Param("vendorId") Long vendorId,
            @Param("status") SubscriptionStatus status,
            @Param("isApproved") Boolean isApproved
    );
    // Check if customer already has active/pending subscription for the SAME plan
    @Query("SELECT COUNT(s) > 0 FROM Subscription s " +
            "WHERE s.customer.customerId = :customerId " +
            "AND s.mealPlan.planId = :planId " +
            "AND s.status IN ('ACTIVE', 'PENDING') " +
            "AND s.endDate >= CURRENT_DATE")
    boolean existsActiveSamePlanByCustomer(
            @Param("customerId") Long customerId,
            @Param("planId") Long planId
    );
    // ✅ Find customer's active subscription (single result)
    @Query("SELECT s FROM Subscription s WHERE s.customer.customerId = :customerId " +
            "AND s.status = 'ACTIVE' AND s.endDate >= CURRENT_DATE " +
            "ORDER BY s.createdAt DESC")
    Optional<Subscription> findActiveSubscriptionByCustomerId(@Param("customerId") Long customerId);

    // ✅ Find customer's subscription with specific vendor
    @Query("SELECT s FROM Subscription s WHERE s.customer.customerId = :customerId " +
            "AND s.vendor.vendorId = :vendorId AND s.status = :status " +
            "ORDER BY s.createdAt DESC")
    Optional<Subscription> findByCustomerIdAndVendorIdAndStatus(
            @Param("customerId") Long customerId,
            @Param("vendorId") Long vendorId,
            @Param("status") SubscriptionStatus status
    );

    // ✅ Find all active students for a vendor
    @Query("SELECT s FROM Subscription s WHERE s.vendor.vendorId = :vendorId " +
            "AND s.status = 'ACTIVE' AND s.isApproved = true " +
            "AND s.endDate >= CURRENT_DATE ORDER BY s.customer.fullName")
    List<Subscription> findActiveStudentsByVendor(@Param("vendorId") Long vendorId);

    // ✅ Check if customer already has active subscription with this vendor
    @Query("SELECT COUNT(s) > 0 FROM Subscription s WHERE s.customer.customerId = :customerId " +
            "AND s.vendor.vendorId = :vendorId AND s.status = 'ACTIVE' " +
            "AND s.endDate >= CURRENT_DATE")
    boolean existsActiveSubscriptionByCustomerAndVendor(
            @Param("customerId") Long customerId,
            @Param("vendorId") Long vendorId
    );
}