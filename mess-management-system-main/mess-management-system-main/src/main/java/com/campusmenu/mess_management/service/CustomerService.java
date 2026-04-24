package com.campusmenu.mess_management.service;

import com.campusmenu.mess_management.entity.Customer;
import com.campusmenu.mess_management.entity.Subscription;
import com.campusmenu.mess_management.entity.Payment;
import com.campusmenu.mess_management.entity.Attendance;
import com.campusmenu.mess_management.repository.CustomerRepository;
import com.campusmenu.mess_management.repository.SubscriptionRepository;
import com.campusmenu.mess_management.repository.PaymentRepository;
import com.campusmenu.mess_management.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional

public class CustomerService {
    private final CustomerRepository customerRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new customer
     */
    public Customer registerCustomer(Customer customer) {
        // Check if email already exists
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Check if phone already exists
        if (customerRepository.existsByPhone(customer.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }

        // Hash password
//        customer.setPasswordHash(passwordEncoder.encode(customer.getPasswordHash()));
//        customer.setIsActive(true);

        String plainPassword = customer.getPasswordHash();
        String encryptedPassword = passwordEncoder.encode(plainPassword);
        customer.setPasswordHash(encryptedPassword);

        customer.setIsActive(true);

        return customerRepository.save(customer);
    }

    /**
     * Login customer
     */
    public Optional<Customer> loginCustomer(String email, String password) {
        Optional<Customer> customer = customerRepository.findActiveByEmail(email);

        if (customer.isPresent() && passwordEncoder.matches(password, customer.get().getPasswordHash())) {
            return customer;
        }

        return Optional.empty();
    }

    /**
     * Get customer by ID
     */
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    /**
     * Get customer by email
     */
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    /**
     * Update customer profile
     */
    public Customer updateCustomerProfile(Long customerId, Customer updatedCustomer) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setFullName(updatedCustomer.getFullName());
        customer.setPhone(updatedCustomer.getPhone());
        customer.setProfileImage(updatedCustomer.getProfileImage());

        return customerRepository.save(customer);
    }

    /**
     * Get customer's active subscriptions
     */
    @Transactional(readOnly = true)
    public List<Subscription> getActiveSubscriptions(Long customerId) {
        return subscriptionRepository.findActiveSubscriptionsByCustomer(customerId);
    }

    /**
     * Get customer's all subscriptions (booking history)
     */
    @Transactional(readOnly = true)
    public List<Subscription> getBookingHistory(Long customerId) {
        return subscriptionRepository.findByCustomer_CustomerId(customerId);
    }

    /**
     * Get customer's payment history
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentHistory(Long customerId) {
        return paymentRepository.findPaymentHistoryByCustomer(customerId);
    }

    /**
     * Get customer's attendance history
     */
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceHistory(Long customerId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findAttendanceHistory(customerId, startDate, endDate);
    }

    /**
     * Get customer's QR code from active subscription
     */
    @Transactional(readOnly = true)
    public String getActiveQRCode(Long customerId) {
        List<Subscription> activeSubscriptions = subscriptionRepository
                .findActiveSubscriptionsByCustomer(customerId);

        if (activeSubscriptions.isEmpty()) {
            throw new RuntimeException("No active subscription found");
        }

        return activeSubscriptions.get(0).getQrCode();
    }

    /**
     * Delete customer account
     */
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }
}
