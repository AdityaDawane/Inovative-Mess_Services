package com.campusmenu.mess_management.service;

import com.campusmenu.mess_management.entity.Subscription;
import com.campusmenu.mess_management.entity.Customer;
import com.campusmenu.mess_management.entity.MealPlan;
import com.campusmenu.mess_management.enums.SubscriptionStatus;
import com.campusmenu.mess_management.repository.SubscriptionRepository;
import com.campusmenu.mess_management.repository.CustomerRepository;
import com.campusmenu.mess_management.repository.MealPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional

public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final MealPlanRepository mealPlanRepository;

    /**
     * Book a meal plan (Create subscription)
     */
    public Subscription bookMealPlan(Long customerId, Long planId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        MealPlan mealPlan = mealPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));

        if (!mealPlan.getIsActive()) {
            throw new RuntimeException("This meal plan is not available");
        }

        // Calculate dates
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(mealPlan.getDurationDays());

        // Generate unique QR code
        String qrCode = generateUniqueQRCode(customerId);

        // Create subscription
        Subscription subscription = Subscription.builder()
                .customer(customer)
                .mealPlan(mealPlan)
                .qrCode(qrCode)
                .startDate(startDate)
                .endDate(endDate)
                .status(SubscriptionStatus.PENDING) // Pending until payment
                .totalAmount(mealPlan.getFinalPrice())
                .build();

        return subscriptionRepository.save(subscription);
    }

    /**
     * Generate unique QR code
     * Format: MESS-{CUSTOMER_ID}-{TIMESTAMP}-{RANDOM}
     */
    private String generateUniqueQRCode(Long customerId) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase();

        String qrCode = String.format("MESS-%06d-%s-%s",
                customerId, timestamp, random);

        // Check uniqueness
        while (subscriptionRepository.existsByQrCode(qrCode)) {
            random = UUID.randomUUID().toString()
                    .substring(0, 8).toUpperCase();
            qrCode = String.format("MESS-%06d-%s-%s",
                    customerId, timestamp, random);
        }

        return qrCode;
    }

    /**
     * Cancel subscription
     */
    public Subscription cancelSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new RuntimeException("Subscription already cancelled");
        }

        if (subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            throw new RuntimeException("Cannot cancel expired subscription");
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        return subscriptionRepository.save(subscription);
    }

    /**
     * Activate subscription (after successful payment)
     */
    public Subscription activateSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        return subscriptionRepository.save(subscription);
    }

    /**
     * Get subscription by ID
     */
    @Transactional(readOnly = true)
    public Subscription getSubscriptionById(Long subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }

    /**
     * Get subscription by QR code
     */
    @Transactional(readOnly = true)
    public Subscription getSubscriptionByQRCode(String qrCode) {
        return subscriptionRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("Invalid QR code"));
    }

    /**
     * Check if subscription is valid
     */
    @Transactional(readOnly = true)
    public boolean isSubscriptionValid(String qrCode) {
        return subscriptionRepository
                .findActiveSubscriptionByQrCode(qrCode, LocalDate.now())
                .isPresent();
    }

    /**
     * Get all subscriptions by customer
     */
    @Transactional(readOnly = true)
    public List<Subscription> getSubscriptionsByCustomer(Long customerId) {
        return subscriptionRepository.findByCustomer_CustomerId(customerId);
    }

    /**
     * Get active subscriptions by customer
     */
    @Transactional(readOnly = true)
    public List<Subscription> getActiveSubscriptionsByCustomer(Long customerId) {
        return subscriptionRepository.findActiveSubscriptionsByCustomer(customerId);
    }

    /**
     * Expire old subscriptions (to be called by scheduled job)
     */
    public int expireOldSubscriptions() {
        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findExpiredActiveSubscriptions();

        for (Subscription subscription : expiredSubscriptions) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
        }

        subscriptionRepository.saveAll(expiredSubscriptions);
        return expiredSubscriptions.size();
    }
}
