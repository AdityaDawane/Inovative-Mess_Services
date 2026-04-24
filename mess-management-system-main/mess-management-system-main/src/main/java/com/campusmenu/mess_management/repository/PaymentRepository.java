package com.campusmenu.mess_management.repository;

import com.campusmenu.mess_management.entity.Payment;
import com.campusmenu.mess_management.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByCustomer_CustomerId(Long customerId);

    List<Payment> findBySubscription_SubscriptionId(Long subscriptionId);

    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.customer.customerId = :customerId " +
            "AND p.status = :status ORDER BY p.paymentDate DESC")
    List<Payment> findByCustomerIdAndStatus(
            @Param("customerId") Long customerId,
            @Param("status") PaymentStatus status
    );

    @Query("SELECT p FROM Payment p WHERE p.customer.customerId = :customerId " +
            "ORDER BY p.paymentDate DESC")
    List<Payment> findPaymentHistoryByCustomer(@Param("customerId") Long customerId);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate " +
            "AND p.status = 'SUCCESS'")
    List<Payment> findSuccessfulPaymentsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' " +
            "AND p.paymentDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenueBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    boolean existsByTransactionId(String transactionId);
}
