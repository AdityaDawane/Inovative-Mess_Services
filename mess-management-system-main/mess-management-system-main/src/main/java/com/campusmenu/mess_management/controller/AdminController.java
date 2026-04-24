 package com.campusmenu.mess_management.controller;

import com.campusmenu.mess_management.dto.request.LoginRequest;
import com.campusmenu.mess_management.dto.response.*;
import com.campusmenu.mess_management.entity.*;
import com.campusmenu.mess_management.security.JwtUtils;
import com.campusmenu.mess_management.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ADMIN CONTROLLER - FIXED VERSION
 * Now uses AdminService for login (not MealPlanService)
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final MealPlanService mealPlanService;
    private final PaymentService paymentService;
    private final CustomerService customerService;
    private final VendorService vendorService;
    private final SubscriptionService subscriptionService;
    private final AdminService adminService;  // ✅ ADDED THIS
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;

    // ══════════════════════════════════════════════════════════
    // AUTHENTICATION - FIXED
    // ══════════════════════════════════════════════════════════

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        try {
            // ✅ NOW USES AdminService.loginAdmin()
            Admin admin = adminService.loginAdmin(
                            request.getEmail(),      // ✅ getEmail() now works
                            request.getPassword()    // ✅ getPassword() now works
                    )
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            String token = jwtUtils.generateToken(
                    admin.getEmail(),
                    admin.getAdminId(),
                    "ADMIN"
            );

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .userId(admin.getAdminId())
                    .fullName(admin.getFullName())
                    .email(admin.getEmail())
                    .role("ADMIN")
                    .message("Login successful!")
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response, "Welcome Admin!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials"));
        }
    }

    // ══════════════════════════════════════════════════════════
    // STUDENT MANAGEMENT (6 endpoints)
    // ══════════════════════════════════════════════════════════

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllStudents() {
        try {
            List<Customer> customers = vendorService.getAllStudents();
            List<CustomerResponse> responses = customers.stream()
                    .map(c -> modelMapper.map(c, CustomerResponse.class))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(responses,
                    "Students retrieved. Total: " + responses.size()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getStudentById(
            @PathVariable Long id) {
        try {
            Customer customer = customerService.getCustomerById(id)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            return ResponseEntity.ok(ApiResponse.success(
                    modelMapper.map(customer, CustomerResponse.class), "Student retrieved"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateStudent(
            @PathVariable Long id,
            @RequestBody Customer updatedCustomer) {
        try {
            Customer customer = customerService.updateCustomerProfile(id, updatedCustomer);
            return ResponseEntity.ok(ApiResponse.success(
                    modelMapper.map(customer, CustomerResponse.class), "Student updated"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        try {
            Customer customer = customerService.getCustomerById(id)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            customer.setIsActive(false);
            customerService.updateCustomerProfile(id, customer);
            return ResponseEntity.ok(ApiResponse.success("Student deactivated"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/students/{id}/toggle-status")
    public ResponseEntity<ApiResponse<CustomerResponse>> toggleStudentStatus(
            @PathVariable Long id) {
        try {
            Customer customer = customerService.getCustomerById(id)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            customer.setIsActive(!customer.getIsActive());
            Customer updated = customerService.updateCustomerProfile(id, customer);

            String message = updated.getIsActive() ?
                    "Student activated" : "Student deactivated";
            return ResponseEntity.ok(ApiResponse.success(
                    modelMapper.map(updated, CustomerResponse.class), message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // VENDOR MANAGEMENT (4 endpoints) - Stubs
    // ══════════════════════════════════════════════════════════

    @GetMapping("/vendors")
    public ResponseEntity<ApiResponse<List<Vendor>>> getAllVendors() {
        try {
            List<Vendor> vendors = List.of();
            return ResponseEntity.ok(ApiResponse.success(vendors,
                    "Vendors retrieved. Total: " + vendors.size()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/vendors")
    public ResponseEntity<ApiResponse<Vendor>> addVendor(
            @Valid @RequestBody Vendor vendor) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(vendor, "Vendor added"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/vendors/{id}")
    public ResponseEntity<ApiResponse<Vendor>> updateVendor(
            @PathVariable Long id,
            @RequestBody Vendor updatedVendor) {
        try {
            return ResponseEntity.ok(ApiResponse.success(updatedVendor, "Vendor updated"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/vendors/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVendor(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Vendor deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // MEAL PLAN MANAGEMENT (6 endpoints)
    // ══════════════════════════════════════════════════════════

    @GetMapping("/meal-plans")
    public ResponseEntity<ApiResponse<List<MealPlanResponse>>> getAllMealPlans() {
        try {
            List<MealPlan> plans = mealPlanService.getAllPlans();
            List<MealPlanResponse> responses = plans.stream()
                    .map(plan -> modelMapper.map(plan, MealPlanResponse.class))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(responses,
                    "Meal plans retrieved. Total: " + responses.size()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/meal-plans/{id}")
    public ResponseEntity<ApiResponse<MealPlanResponse>> getMealPlanById(
            @PathVariable Long id) {
        try {
            MealPlan plan = mealPlanService.getMealPlanById(id);
            MealPlanResponse response = modelMapper.map(plan, MealPlanResponse.class);
            return ResponseEntity.ok(ApiResponse.success(response, "Meal plan retrieved"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/meal-plans")
    public ResponseEntity<ApiResponse<MealPlanResponse>> createMealPlan(
            @Valid @RequestBody MealPlan mealPlan) {
        try {
            MealPlan savedPlan = mealPlanService.createMealPlan(mealPlan);
            MealPlanResponse response = modelMapper.map(savedPlan, MealPlanResponse.class);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Meal plan created!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/meal-plans/{id}")
    public ResponseEntity<ApiResponse<MealPlanResponse>> updateMealPlan(
            @PathVariable Long id,
            @Valid @RequestBody MealPlan updatedPlan) {
        try {
            MealPlan plan = mealPlanService.updateMealPlan(id, updatedPlan);
            MealPlanResponse response = modelMapper.map(plan, MealPlanResponse.class);
            return ResponseEntity.ok(ApiResponse.success(response, "Meal plan updated!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/meal-plans/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMealPlan(@PathVariable Long id) {
        try {
            mealPlanService.deleteMealPlan(id);
            return ResponseEntity.ok(ApiResponse.success("Meal plan deleted!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/meal-plans/{id}/toggle-status")
    public ResponseEntity<ApiResponse<MealPlanResponse>> toggleMealPlanStatus(
            @PathVariable Long id) {
        try {
            MealPlan plan = mealPlanService.togglePlanStatus(id);
            MealPlanResponse response = modelMapper.map(plan, MealPlanResponse.class);
            String message = plan.getIsActive() ? "Plan activated!" : "Plan deactivated!";
            return ResponseEntity.ok(ApiResponse.success(response, message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // PAYMENT MANAGEMENT (3 endpoints)
    // ══════════════════════════════════════════════════════════

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        try {
            List<Payment> payments = List.of();
            List<PaymentResponse> responses = payments.stream()
                    .map(this::toPaymentResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(responses,
                    "Payments retrieved. Total: " + responses.size()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/payments/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable Long id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(ApiResponse.success(
                    toPaymentResponse(payment), "Payment retrieved"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/payments/range")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Payment> payments = paymentService.getSuccessfulPayments(startDate, endDate);
            List<PaymentResponse> responses = payments.stream()
                    .map(this::toPaymentResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(responses,
                    "Payments retrieved. Total: " + responses.size()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // REPORTS & ANALYTICS (4 endpoints)
    // ══════════════════════════════════════════════════════════

    @GetMapping("/reports/revenue")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            if (startDate == null) startDate = LocalDateTime.now().minusDays(30);
            if (endDate == null) endDate = LocalDateTime.now();

            Double totalRevenue = paymentService.getTotalRevenue(startDate, endDate);
            List<Payment> payments = paymentService.getSuccessfulPayments(startDate, endDate);

            Map<String, Object> report = new HashMap<>();
            report.put("fromDate", startDate);
            report.put("toDate", endDate);
            report.put("totalRevenue", totalRevenue);
            report.put("currency", "INR");
            report.put("totalTransactions", payments.size());
            report.put("averageTransaction", payments.isEmpty() ? 0 : totalRevenue / payments.size());

            return ResponseEntity.ok(ApiResponse.success(report, "Revenue report generated"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/reports/subscriptions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSubscriptionReport() {
        try {
            Map<String, Object> report = new HashMap<>();
            report.put("totalActive", 0);
            report.put("expiringThisWeek", 0);
            report.put("planWiseBreakdown", new HashMap<>());
            report.put("message", "Subscription report - implement with SubscriptionService");

            return ResponseEntity.ok(ApiResponse.success(report, "Subscription report"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/reports/attendance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAttendanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Map<String, Object> report = new HashMap<>();
            report.put("fromDate", startDate);
            report.put("toDate", endDate);
            report.put("totalScans", 0);
            report.put("message", "Attendance report - implement with AttendanceService");

            return ResponseEntity.ok(ApiResponse.success(report, "Attendance report"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            List<Customer> allStudents = vendorService.getAllStudents();
            long activeStudents = allStudents.stream()
                    .filter(Customer::getIsActive).count();
            stats.put("totalStudents", allStudents.size());
            stats.put("activeStudents", activeStudents);

            List<MealPlan> plans = mealPlanService.getAllActivePlans();
            stats.put("activeMealPlans", plans.size());

            Double todayRevenue = paymentService.getTotalRevenue(
                    LocalDateTime.now().withHour(0).withMinute(0),
                    LocalDateTime.now()
            );
            stats.put("todayRevenue", todayRevenue);
            stats.put("generatedAt", LocalDateTime.now());

            return ResponseEntity.ok(ApiResponse.success(stats, "Dashboard statistics"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // SYSTEM SETTINGS (2 endpoints)
    // ══════════════════════════════════════════════════════════

    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemSettings() {
        try {
            Map<String, Object> settings = new HashMap<>();
            settings.put("appName", "Mess Management System");
            settings.put("version", "1.0.0");
            settings.put("message", "System settings - implement with SystemSettingsService");

            return ResponseEntity.ok(ApiResponse.success(settings, "System settings retrieved"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateSystemSettings(
            @RequestBody Map<String, Object> settings) {
        try {
            return ResponseEntity.ok(ApiResponse.success(settings, "Settings updated"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // HELPER METHODS
    // ══════════════════════════════════════════════════════════

    private PaymentResponse toPaymentResponse(Payment p) {
        return PaymentResponse.builder()
                .paymentId(p.getPaymentId())
                .subscriptionId(p.getSubscription().getSubscriptionId())
                .customerId(p.getCustomer().getCustomerId())
                .customerName(p.getCustomer().getFullName())
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .transactionId(p.getTransactionId())
                .status(p.getStatus())
                .paymentDate(p.getPaymentDate())
                .build();
    }
}
