package com.campusmenu.mess_management.service;
import com.campusmenu.mess_management.entity.Vendor;
import com.campusmenu.mess_management.entity.DailyMenu;
import com.campusmenu.mess_management.entity.Attendance;
import com.campusmenu.mess_management.entity.Customer;
import com.campusmenu.mess_management.entity.Subscription;
import com.campusmenu.mess_management.enums.MealType;
import com.campusmenu.mess_management.repository.VendorRepository;
import com.campusmenu.mess_management.repository.DailyMenuRepository;
import com.campusmenu.mess_management.repository.AttendanceRepository;
import com.campusmenu.mess_management.repository.CustomerRepository;
import com.campusmenu.mess_management.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional

public class VendorService {

    private final VendorRepository vendorRepository;
    private final DailyMenuRepository dailyMenuRepository;
    private final AttendanceRepository attendanceRepository;
    private final CustomerRepository customerRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Login vendor
     */
    public Optional<Vendor> loginVendor(String email, String password) {
        Optional<Vendor> vendor = vendorRepository.findActiveByEmail(email);

        if (vendor.isPresent() && passwordEncoder.matches(password, vendor.get().getPasswordHash())) {
            // Update last login
            vendor.get().setLastLogin(LocalDateTime.now());
            vendorRepository.save(vendor.get());
            return vendor;
        }

        return Optional.empty();
    }

    /**
     * Get vendor by ID
     */
    @Transactional(readOnly = true)
    public Optional<Vendor> getVendorById(Long vendorId) {
        return vendorRepository.findById(vendorId);
    }

    /**
     * Add new menu
     */
    public DailyMenu addMenu(DailyMenu menu) {
        // Check if menu already exists for this date and meal type
        if (dailyMenuRepository.existsByMenuDateAndMealType(menu.getMenuDate(), menu.getMealType())) {
            throw new RuntimeException("Menu already exists for this date and meal type");
        }

        return dailyMenuRepository.save(menu);
    }

    /**
     * Update existing menu
     */
    public DailyMenu updateMenu(Long menuId, DailyMenu updatedMenu) {
        DailyMenu menu = dailyMenuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found"));

        menu.setItems(updatedMenu.getItems());
        menu.setDescription(updatedMenu.getDescription());
        menu.setIsAvailable(updatedMenu.getIsAvailable());

        return dailyMenuRepository.save(menu);
    }

    /**
     * Delete menu
     */
    public void deleteMenu(Long menuId) {
        dailyMenuRepository.deleteById(menuId);
    }

    /**
     * Get today's menu
     */
    @Transactional(readOnly = true)
    public List<DailyMenu> getTodaysMenu() {
        return dailyMenuRepository.findTodaysMenu();
    }

    /**
     * Get menu by date
     */
    @Transactional(readOnly = true)
    public List<DailyMenu> getMenuByDate(LocalDate date) {
        return dailyMenuRepository.findAvailableMenuByDate(date);
    }

    /**
     * Scan student QR code and mark attendance
     */
    public Attendance scanQRCode(String qrCode, MealType mealType, Long vendorId, String machineId) {
        // Find active subscription by QR code
        Subscription subscription = subscriptionRepository
                .findActiveSubscriptionByQrCode(qrCode, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired QR code"));

        Customer customer = subscription.getCustomer();

        // Check if meal type is included in plan
        Boolean mealIncluded = subscription.getMealPlan()
                .getMealsIncluded()
                .get(mealType.name().toLowerCase());

        if (mealIncluded == null || !mealIncluded) {
            throw new RuntimeException(mealType + " is not included in this plan");
        }

        // Check if already scanned today for this meal
        if (attendanceRepository.existsByCustomer_CustomerIdAndMealTypeAndScanDate(
                customer.getCustomerId(), mealType, LocalDate.now())) {
            throw new RuntimeException("Already scanned for " + mealType + " today");
        }

        // Get vendor
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        // Create attendance record
        Attendance attendance = Attendance.builder()
                .subscription(subscription)
                .customer(customer)
                .mealType(mealType)
                .scanDate(LocalDate.now())
                .scanTime(LocalDateTime.now())
                .scannedByVendor(vendor)
                .machineId(machineId)
                .isValid(true)
                .build();

        return attendanceRepository.save(attendance);
    }

    /**
     * Get today's attendance
     */
    @Transactional(readOnly = true)
    public List<Attendance> getTodaysAttendance() {
        return attendanceRepository.findTodaysAttendance();
    }

    /**
     * Get attendance by vendor and date
     */
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByVendorAndDate(Long vendorId, LocalDate date) {
        return attendanceRepository.findAttendanceByVendorAndDate(vendorId, date);
    }

    /**
     * View all students (customers)
     */
    @Transactional(readOnly = true)
    public List<Customer> getAllStudents() {
        return customerRepository.findAll();
    }

    /**
     * View student bookings (subscriptions)
     */
    @Transactional(readOnly = true)
    public List<Subscription> getStudentBookings(Long customerId) {
        return subscriptionRepository.findByCustomer_CustomerId(customerId);
    }

    /**
     * Get attendance count by meal type
     */
    @Transactional(readOnly = true)
    public Long getAttendanceCount(LocalDate date, MealType mealType) {
        return attendanceRepository.countAttendanceByDateAndMealType(date, mealType);
    }
}
