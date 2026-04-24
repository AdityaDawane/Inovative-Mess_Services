package com.campusmenu.mess_management.service;
import com.campusmenu.mess_management.entity.Payment;
import com.campusmenu.mess_management.entity.Subscription;
import com.campusmenu.mess_management.entity.Customer;
import com.campusmenu.mess_management.enums.PaymentStatus;
import com.campusmenu.mess_management.enums.PaymentMethod;
import com.campusmenu.mess_management.repository.PaymentRepository;
import com.campusmenu.mess_management.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional

public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;

    /**
     * Create payment for subscription
     */
    public Payment createPayment(Long subscriptionId, PaymentMethod paymentMethod, String paymentGateway) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        Customer customer = subscription.getCustomer();

        // Generate unique transaction ID
        String transactionId = generateTransactionId();

        Payment payment = Payment.builder()
                .subscription(subscription)
                .customer(customer)
                .amount(subscription.getTotalAmount())
                .paymentMethod(paymentMethod)
                .paymentGateway(paymentGateway)
                .transactionId(transactionId)
                .status(PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }

    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        String txnId = "TXN" + System.currentTimeMillis() +
                UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        while (paymentRepository.existsByTransactionId(txnId)) {
            txnId = "TXN" + System.currentTimeMillis() +
                    UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        }

        return txnId;
    }

    /**
     * Update payment status to SUCCESS
     */
    public Payment markPaymentSuccess(Long paymentId, String gatewayPaymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setGatewayPaymentId(gatewayPaymentId);

        paymentRepository.save(payment);

        // Activate subscription
        subscriptionService.activateSubscription(payment.getSubscription().getSubscriptionId());

        return payment;
    }

    /**
     * Update payment status to FAILED
     */
    public Payment markPaymentFailed(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(PaymentStatus.FAILED);
        return paymentRepository.save(payment);
    }

    /**
     * Get payment by ID
     */
    @Transactional(readOnly = true)
    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    /**
     * Get payment by transaction ID
     */
    @Transactional(readOnly = true)
    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    /**
     * Get payment history by customer
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentHistoryByCustomer(Long customerId) {
        return paymentRepository.findPaymentHistoryByCustomer(customerId);
    }

    /**
     * Get payments by subscription
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsBySubscription(Long subscriptionId) {
        return paymentRepository.findBySubscription_SubscriptionId(subscriptionId);
    }

    /**
     * Get payment status
     */
    @Transactional(readOnly = true)
    public PaymentStatus getPaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return payment.getStatus();
    }

    /**
     * Get total revenue between dates
     */
    @Transactional(readOnly = true)
    public Double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        Double revenue = paymentRepository.getTotalRevenueBetweenDates(startDate, endDate);
        return revenue != null ? revenue : 0.0;
    }

    /**
     * Get all successful payments
     */
    @Transactional(readOnly = true)
    public List<Payment> getSuccessfulPayments(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findSuccessfulPaymentsBetweenDates(startDate, endDate);
    }
}
