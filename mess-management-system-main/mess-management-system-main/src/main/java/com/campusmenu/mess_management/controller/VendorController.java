//
//package com.campusmenu.mess_management.controller;
//
//import com.campusmenu.mess_management.dto.request.*;
//import com.campusmenu.mess_management.dto.response.*;
//import com.campusmenu.mess_management.entity.*;
//import com.campusmenu.mess_management.enums.MealType;
//import com.campusmenu.mess_management.security.JwtUtils;
//import com.campusmenu.mess_management.service.VendorService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/vendor")
//@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
//public class VendorController {
//
//    private final VendorService vendorService;
//    private final ModelMapper modelMapper;
//    private final JwtUtils jwtUtils;
//
//    // LOGIN
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
//        try {
//            Vendor vendor = vendorService.loginVendor(request.getEmail(), request.getPassword())
//                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));
//
//            String token = jwtUtils.generateToken(vendor.getEmail(), vendor.getVendorId(), "VENDOR");
//
//            LoginResponse response = LoginResponse.builder()
//                    .token(token)
//                    .tokenType("Bearer")
//                    .userId(vendor.getVendorId())
//                    .fullName(vendor.getFullName())
//                    .email(vendor.getEmail())
//                    .role("VENDOR")
//                    .message("Login successful!")
//                    .build();
//
//            return ResponseEntity.ok(ApiResponse.success(response, "Welcome " + vendor.getFullName() + "!"));
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid credentials"));
//        }
//    }
//
//    // GET PROFILE
//    @GetMapping("/profile/{vendorId}")
//    public ResponseEntity<ApiResponse<Vendor>> getProfile(@PathVariable Long vendorId) {
//        try {
//            Vendor vendor = vendorService.getVendorById(vendorId).orElseThrow(() -> new RuntimeException("Vendor not found"));
//            return ResponseEntity.ok(ApiResponse.success(vendor, "Profile retrieved"));
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
//        }
//    }
//
//    // ADD MENU (both /menu and /menu/create work!)
//    @PostMapping({"/menu", "/menu/create"})
//    public ResponseEntity<ApiResponse<MenuResponse>> addMenu(@Valid @RequestBody MenuRequest request) {
//        try {
//            DailyMenu menu = DailyMenu.builder()
//                    .menuDate(request.getMenuDate())
//                    .mealType(request.getMealType())
//                    .items(request.getItems())
//                    .description(request.getDescription())
//                    .isAvailable(request.getIsAvailable())
//                    .build();
//
//            Vendor vendor = vendorService.getVendorById(request.getVendorId())
//                    .orElseThrow(() -> new RuntimeException("Vendor not found"));
//            menu.setVendor(vendor);
//
//            DailyMenu savedMenu = vendorService.addMenu(menu);
//            MenuResponse response = mapToMenuResponse(savedMenu);
//
//            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Menu added successfully!"));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }
//
//    // UPDATE MENU
//    @PutMapping("/menu/{menuId}")
//    public ResponseEntity<ApiResponse<MenuResponse>> updateMenu(@PathVariable Long menuId, @Valid @RequestBody MenuRequest request) {
//        try {
//            DailyMenu updatedMenu = DailyMenu.builder()
//                    .items(request.getItems())
//                    .description(request.getDescription())
//                    .isAvailable(request.getIsAvailable())
//                    .build();
//
//            DailyMenu menu = vendorService.updateMenu(menuId, updatedMenu);
//            MenuResponse response = mapToMenuResponse(menu);
//
//            return ResponseEntity.ok(ApiResponse.success(response, "Menu updated successfully!"));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }
//
//    // DELETE MENU
//    @DeleteMapping("/menu/{menuId}")
//    public ResponseEntity<ApiResponse<Void>> deleteMenu(@PathVariable Long menuId) {
//        try {
//            vendorService.deleteMenu(menuId);
//            return ResponseEntity.ok(ApiResponse.success(null, "Menu deleted successfully!"));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }
//
//    // GET TODAY'S MENU
//    @GetMapping("/menu/today")
//    public ResponseEntity<ApiResponse<List<MenuResponse>>> getTodaysMenu() {
//        try {
//            List<DailyMenu> menus = vendorService.getTodaysMenu();
//            List<MenuResponse> responses = menus.stream().map(this::mapToMenuResponse).collect(Collectors.toList());
//            return ResponseEntity.ok(ApiResponse.success(responses, "Today's menu retrieved"));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }
//
//    // GET MENU BY DATE
//    @GetMapping("/menu")
//    public ResponseEntity<ApiResponse<List<MenuResponse>>> getMenuByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//        try {
//            List<DailyMenu> menus = vendorService.getMenuByDate(date);
//            List<MenuResponse> responses = menus.stream().map(this::mapToMenuResponse).collect(Collectors.toList());
//            return ResponseEntity.ok(ApiResponse.success(responses, "Menu retrieved"));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }
//
//    // SCAN QR CODE
//    @PostMapping("/scan-qr")
//    public ResponseEntity<ApiResponse<AttendanceResponse>> scanQRCode(@Valid @RequestBody QRScanRequest request) {
//        try {
//            Attendance attendance = vendorService.scanQRCode(request.getQrCode(), request.getMealType(), request.getVendorId(), request.getMachineId());
//
//            AttendanceResponse response = AttendanceResponse.builder()
//                    .attendanceId(attendance.getAttendanceId())
//                    .customerId(attendance.getCustomer().getCustomerId())
//                    .customerName(attendance.getCustomer().getFullName())
//                    .mealType(attendance.getMealType())
//                    .scanDate(attendance.getScanDate())
//                    .scanTime(attendance.getScanTime())
//                    .vendorName(attendance.getScannedByVendor().getFullName())
//                    .messName(attendance.getScannedByVendor().getMessName())
//                    .isValid(true)
//                    .message("Welcome " + attendance.getCustomer().getFullName() + "! Enjoy your " + attendance.getMealType().toString().toLowerCase() + "!")
//                    .build();
//
//            return ResponseEntity.ok(ApiResponse.success(response, "Attendance marked successfully!"));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error("QR Scan Failed: " + e.getMessage()));
//        }
//    }
//
//    // GET TODAY'S ATTENDANCE
//    @GetMapping("/attendance/today")
//    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getTodaysAttendance() {
//        try {
//            List<Attendance> attendances = vendorService.getTodaysAttendance();
//            List<AttendanceResponse> responses = attendances.stream().map(this::mapToAttendanceResponse).collect(Collectors.toList());
//            return ResponseEntity.ok(ApiResponse.success(responses, "Today's attendance retrieved. Total scans: " + responses.size()));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }
//
//    // GET ATTENDANCE BY DATE
//    @GetMapping("/attendance")
//    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getAttendanceByDate(@RequestParam Long vendorId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//        try {
//            List<Attendance> attendances = vendorService.getAttendanceByVendorAndDate(vendorId, date);
//            List<AttendanceResponse> responses = attendances.stream().map(this::mapToAttendanceResponse).collect(Collectors.toList());
//            return ResponseEntity.ok(ApiResponse.success(responses, "Attendance retrieved"));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }
//
//    // GET ATTENDANCE COUNT
//    @GetMapping("/attendance/count")
//    public ResponseEntity<ApiResponse<Long>> getAttendanceCount(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam MealType mealType) {
//        try {
//            Long count = vendorService.getAttendanceCount(date, mealType);
//            return ResponseEntity.ok(ApiResponse.success(count, "Total " + mealType + " scans on " + date + ": " + count));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }
//
//    // GET ALL STUDENTS
//    @GetMapping("/students")
//    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllStudents() {
//        try {
//            List<Customer> customers = vendorService.getAllStudents();
//            List<CustomerResponse> responses = customers.stream().map(c -> modelMapper.map(c, CustomerResponse.class)).collect(Collectors.toList());
//            return ResponseEntity.ok(ApiResponse.success(responses, "Students retrieved. Total: " + responses.size()));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }
//
//    // GET STUDENT BOOKINGS
//    @GetMapping("/student-bookings/{customerId}")
//    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getStudentBookings(@PathVariable Long customerId) {
//        try {
//            List<Subscription> subscriptions = vendorService.getStudentBookings(customerId);
//            List<SubscriptionResponse> responses = subscriptions.stream().map(this::toSubResponse).collect(Collectors.toList());
//            return ResponseEntity.ok(ApiResponse.success(responses, "Student bookings retrieved"));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }
//
//    // HELPER METHODS
//    private MenuResponse mapToMenuResponse(DailyMenu menu) {
//        return MenuResponse.builder()
//                .menuId(menu.getMenuId())
//                .menuDate(menu.getMenuDate())
//                .mealType(menu.getMealType())
//                .items(menu.getItems())
//                .description(menu.getDescription())
//                .isAvailable(menu.getIsAvailable())
//                .vendorName(menu.getVendor().getFullName())
//                .messName(menu.getVendor().getMessName())
//                .build();
//    }
//
//    private AttendanceResponse mapToAttendanceResponse(Attendance attendance) {
//        return AttendanceResponse.builder()
//                .attendanceId(attendance.getAttendanceId())
//                .customerId(attendance.getCustomer().getCustomerId())
//                .customerName(attendance.getCustomer().getFullName())
//                .mealType(attendance.getMealType())
//                .scanDate(attendance.getScanDate())
//                .scanTime(attendance.getScanTime())
//                .vendorName(attendance.getScannedByVendor().getFullName())
//                .messName(attendance.getScannedByVendor().getMessName())
//                .isValid(attendance.getIsValid())
//                .message("Attendance recorded")
//                .build();
//    }
//
//    private SubscriptionResponse toSubResponse(Subscription s) {
//        return SubscriptionResponse.builder()
//                .subscriptionId(s.getSubscriptionId())
//                .customerId(s.getCustomer().getCustomerId())
//                .customerName(s.getCustomer().getFullName())
//                .planId(s.getMealPlan().getPlanId())
//                .planName(s.getMealPlan().getPlanName())
//                .qrCode(s.getQrCode())
//                .startDate(s.getStartDate())
//                .endDate(s.getEndDate())
//                .status(s.getStatus())
//                .totalAmount(s.getTotalAmount())
//                .daysRemaining(s.getDaysRemaining())
//                .build();
//    }
//}
package com.campusmenu.mess_management.controller;
import com.campusmenu.mess_management.enums.SubscriptionStatus;
import com.campusmenu.mess_management.dto.response.MealPlanResponse;
import com.campusmenu.mess_management.repository.CustomerRepository;
import com.campusmenu.mess_management.repository.SubscriptionRepository;
import com.campusmenu.mess_management.repository.VendorRepository;
import com.campusmenu.mess_management.dto.request.*;
import com.campusmenu.mess_management.dto.response.*;
import com.campusmenu.mess_management.entity.*;
import com.campusmenu.mess_management.enums.MealType;
import com.campusmenu.mess_management.enums.PlanType;
import com.campusmenu.mess_management.repository.MealPlanRepository;
import com.campusmenu.mess_management.security.JwtUtils;
import com.campusmenu.mess_management.service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.campusmenu.mess_management.enums.MealType;
import java.util.Base64;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@RequestMapping("/api/vendor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VendorController {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;


    private final VendorService vendorService;
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;

    @Autowired
    private MealPlanRepository mealPlanRepository;

    private MealPlanResponse mapToMealPlanResponse(MealPlan plan) {
        return MealPlanResponse.builder()
                .planId(plan.getPlanId())
                .planName(plan.getPlanName())
                .planType(plan.getPlanType())
                .durationDays(plan.getDurationDays())
                .price(plan.getPrice())
                .discountPercentage(plan.getDiscountPercentage())
                .finalPrice(plan.getFinalPrice())
                .mealsIncluded(plan.getMealsIncluded())
                .description(plan.getDescription())
                .isActive(plan.getIsActive())
                .build();
    }

    // ==================== LOGIN ====================
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Vendor vendor = vendorService.loginVendor(request.getEmail(), request.getPassword())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            String token = jwtUtils.generateToken(vendor.getEmail(), vendor.getVendorId(), "VENDOR");

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .userId(vendor.getVendorId())
                    .fullName(vendor.getFullName())
                    .email(vendor.getEmail())
                    .role("VENDOR")
                    .message("Login successful!")
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response, "Welcome!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid credentials"));
        }
    }

    // ==================== MEAL PLAN MANAGEMENT ====================

    @PostMapping({"/meal-plans/create", "/meal-plans"})
    public ResponseEntity<ApiResponse<MealPlan>> createMealPlan(@RequestBody Map<String, Object> request) {
        try {
            MealPlan plan = new MealPlan();
            plan.setPlanName((String) request.get("planName"));
            plan.setPlanType(PlanType.valueOf((String) request.get("planType")));
            plan.setDurationDays(((Number) request.get("durationDays")).intValue());

            // Use BigDecimal for prices
            plan.setPrice(BigDecimal.valueOf(((Number) request.get("price")).doubleValue()));
            plan.setDiscountPercentage(BigDecimal.valueOf(((Number) request.get("discountPercentage")).doubleValue()));
            plan.setFinalPrice(BigDecimal.valueOf(((Number) request.get("finalPrice")).doubleValue()));

            // Parse mealsIncluded - handle both String and Map
            Object mealsIncludedObj = request.get("mealsIncluded");
            Map<String, Boolean> mealsMap = new HashMap<>();

            if (mealsIncludedObj instanceof String) {
                // If it's a string like "breakfast,lunch,dinner"
                String mealsStr = (String) mealsIncludedObj;
                String[] meals = mealsStr.toLowerCase().split(",");
                for (String meal : meals) {
                    mealsMap.put(meal.trim(), true);
                }
            } else if (mealsIncludedObj instanceof Map) {
                // If it's already a map
                mealsMap = (Map<String, Boolean>) mealsIncludedObj;
            }

            plan.setMealsIncluded(mealsMap);
            plan.setDescription((String) request.get("description"));
            plan.setIsActive(true);
            plan.setCreatedAt(LocalDateTime.now());
            plan.setUpdatedAt(LocalDateTime.now());

            MealPlan savedPlan = mealPlanRepository.save(plan);

            return ResponseEntity.ok(ApiResponse.success(savedPlan, "Meal plan created successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/meal-plans")
    public ResponseEntity<ApiResponse<List<MealPlanResponse>>> getAllMealPlans() {
        try {
            List<MealPlan> plans = mealPlanRepository.findAll();
            List<MealPlanResponse> response = plans.stream()
                    .map(this::mapToMealPlanResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(response, "Meal plans retrieved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }


    @DeleteMapping("/meal-plans/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMealPlan(@PathVariable Long id) {
        try {
            if (!mealPlanRepository.existsById(id)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Meal plan not found"));
            }
            mealPlanRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Meal plan deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== MENU MANAGEMENT ====================

    @PostMapping({"/menu", "/menu/create"})
    public ResponseEntity<ApiResponse<MenuResponse>> addMenu(@Valid @RequestBody MenuRequest request) {
        try {
            DailyMenu menu = DailyMenu.builder()
                    .menuDate(request.getMenuDate())
                    .mealType(request.getMealType())
                    .items(request.getItems())
                    .description(request.getDescription())
                    .isAvailable(request.getIsAvailable())
                    .build();

            Vendor vendor = vendorService.getVendorById(request.getVendorId())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));
            menu.setVendor(vendor);

            DailyMenu savedMenu = vendorService.addMenu(menu);
            MenuResponse response = mapToMenuResponse(savedMenu);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Menu added!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/menu/today")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getTodaysMenu() {
        try {
            List<DailyMenu> menus = vendorService.getTodaysMenu();
            List<MenuResponse> responses = menus.stream().map(this::mapToMenuResponse).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(responses, "Menu retrieved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/menu/{menuId}")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(@PathVariable Long menuId) {
        try {
            vendorService.deleteMenu(menuId);
            return ResponseEntity.ok(ApiResponse.success(null, "Menu deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== QR SCANNING ====================

    @PostMapping("/scan-qr")
    public ResponseEntity<ApiResponse<AttendanceResponse>> scanQRCode(@Valid @RequestBody QRScanRequest request) {
        try {
            Attendance attendance = vendorService.scanQRCode(request.getQrCode(), request.getMealType(),
                    request.getVendorId(), request.getMachineId());

            AttendanceResponse response = AttendanceResponse.builder()
                    .attendanceId(attendance.getAttendanceId())
                    .customerId(attendance.getCustomer().getCustomerId())
                    .customerName(attendance.getCustomer().getFullName())
                    .mealType(attendance.getMealType())
                    .scanDate(attendance.getScanDate())
                    .scanTime(attendance.getScanTime())
                    .vendorName(attendance.getScannedByVendor().getFullName())
                    .messName(attendance.getScannedByVendor().getMessName())
                    .isValid(true)
                    .message("Welcome " + attendance.getCustomer().getFullName() + "!")
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response, "Attendance marked!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("QR Scan Failed: " + e.getMessage()));
        }
    }

    @GetMapping("/attendance/today")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getTodaysAttendance() {
        try {
            List<Attendance> attendances = vendorService.getTodaysAttendance();
            List<AttendanceResponse> responses = attendances.stream().map(this::mapToAttendanceResponse).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(responses, "Attendance retrieved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== HELPER METHODS ====================

    private MenuResponse mapToMenuResponse(DailyMenu menu) {
        return MenuResponse.builder()
                .menuId(menu.getMenuId())
                .menuDate(menu.getMenuDate())
                .mealType(menu.getMealType())
                .items(menu.getItems())
                .description(menu.getDescription())
                .isAvailable(menu.getIsAvailable())
                .vendorName(menu.getVendor().getFullName())
                .messName(menu.getVendor().getMessName())
                .build();
    }

    private AttendanceResponse mapToAttendanceResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .attendanceId(attendance.getAttendanceId())
                .customerId(attendance.getCustomer().getCustomerId())
                .customerName(attendance.getCustomer().getFullName())
                .mealType(attendance.getMealType())
                .scanDate(attendance.getScanDate())
                .scanTime(attendance.getScanTime())
                .vendorName(attendance.getScannedByVendor().getFullName())
                .messName(attendance.getScannedByVendor().getMessName())
                .isValid(attendance.getIsValid())
                .message("Attendance recorded")
                .build();
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<VendorResponse>>> getAllVendors() {
        try {
            List<Vendor> vendors = vendorRepository.findAll();
            List<VendorResponse> responses = vendors.stream()
                    .map(this::mapToVendorResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(responses, "Vendors retrieved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private VendorResponse mapToVendorResponse(Vendor vendor) {
        return VendorResponse.builder()
                .vendorId(vendor.getVendorId())
                .fullName(vendor.getFullName())
                .messName(vendor.getMessName())
                .email(vendor.getEmail())
                .phone(vendor.getPhone())
                .isActive(vendor.getIsActive())
                .build();
    }

    @GetMapping("/pending-payments")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPendingPayments(
            @RequestParam Long vendorId) {
        try {
            List<Subscription> pending = subscriptionRepository
                    .findByVendorIdAndStatus(vendorId, SubscriptionStatus.PENDING);

            List<Map<String, Object>> responses = pending.stream().map(sub -> {
                Map<String, Object> map = new HashMap<>();
                map.put("subscriptionId", sub.getSubscriptionId());
                map.put("customerName", sub.getCustomer().getFullName());
                map.put("customerEmail", sub.getCustomer().getEmail());
                map.put("customerPhone", sub.getCustomer().getPhone());
                map.put("planName", sub.getMealPlan().getPlanName());
                map.put("planType", sub.getMealPlan().getPlanType());
                map.put("amount", sub.getTotalAmount());
                map.put("paymentId", sub.getPaymentId());
                map.put("paymentDate", sub.getPaymentDate());
                map.put("startDate", sub.getStartDate());
                map.put("endDate", sub.getEndDate());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(responses,
                    "Pending payments: " + responses.size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/approve-payment")
    public ResponseEntity<ApiResponse<String>> approvePayment(@RequestBody Map<String, Object> request) {
        try {
            Long subscriptionId = ((Number) request.get("subscriptionId")).longValue();
            Long vendorId = ((Number) request.get("vendorId")).longValue();

            Subscription sub = subscriptionRepository.findById(subscriptionId)
                    .orElseThrow(() -> new RuntimeException("Not found"));

            if (!sub.getVendor().getVendorId().equals(vendorId)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Unauthorized"));
            }

            // Approve
            sub.setIsApproved(true);
            sub.setStatus(SubscriptionStatus.ACTIVE);
            sub.setApprovedByVendorId(vendorId);
            sub.setApprovedAt(LocalDateTime.now());

            // Generate QR
            String qrCode = "MESS-" + String.format("%06d", sub.getSubscriptionId()) +
                    "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            sub.setQrCode(qrCode);

            sub.setUpdatedAt(LocalDateTime.now());
            subscriptionRepository.save(sub);

            return ResponseEntity.ok(ApiResponse.success("Approved!",
                    sub.getCustomer().getFullName() + " added to your list"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/reject-payment")
    public ResponseEntity<ApiResponse<String>> rejectPayment(@RequestBody Map<String, Object> request) {
        try {
            Long subscriptionId = ((Number) request.get("subscriptionId")).longValue();
            Long vendorId = ((Number) request.get("vendorId")).longValue();
            String reason = (String) request.get("reason");

            Subscription sub = subscriptionRepository.findById(subscriptionId)
                    .orElseThrow(() -> new RuntimeException("Not found"));

            if (!sub.getVendor().getVendorId().equals(vendorId)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Unauthorized"));
            }

            sub.setStatus(SubscriptionStatus.REJECTED);
            sub.setRejectionReason(reason);
            sub.setUpdatedAt(LocalDateTime.now());
            subscriptionRepository.save(sub);

            return ResponseEntity.ok(ApiResponse.success("Rejected", reason));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getVendorStudents(
            @RequestParam Long vendorId) {
        try {
            List<Subscription> subscriptions = subscriptionRepository
                    .findByVendorIdAndStatus(vendorId, SubscriptionStatus.ACTIVE);

            List<Map<String, Object>> students = subscriptions.stream().map(sub -> {
                Map<String, Object> map = new HashMap<>();
                map.put("customerId", sub.getCustomer().getCustomerId());
                map.put("fullName", sub.getCustomer().getFullName());
                map.put("email", sub.getCustomer().getEmail());
                map.put("phone", sub.getCustomer().getPhone());
                map.put("planName", sub.getMealPlan().getPlanName());
                map.put("subscriptionStatus", sub.getStatus().toString());
                map.put("startDate", sub.getStartDate().toString());
                map.put("endDate", sub.getEndDate().toString());
                map.put("daysRemaining", sub.getDaysRemaining());
                map.put("qrCode", sub.getQrCode());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(students,
                    "Students retrieved: " + students.size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    // ── 1. Generate / fetch today's session QR for a meal ──────────────
    @GetMapping("/session-qr")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSessionQR(
            @RequestParam Long vendorId,
            @RequestParam MealType mealType) {
        try {
            // Session QR = deterministic per vendor + meal + date
            // No DB table needed — regenerate the same code from these 3 values
            String today = LocalDate.now().toString();             // e.g. "2026-04-18"
            String raw   = "SESSION-" + vendorId + "-" + mealType + "-" + today;
            String sessionQR = Base64.getEncoder()
                    .encodeToString(raw.getBytes())
                    .replaceAll("=", "");                          // clean base64

            // Meal time windows
            Map<String, String> windows = Map.of(
                    "BREAKFAST", "06:00 – 10:00",
                    "LUNCH",     "11:00 – 15:00",
                    "DINNER",    "19:00 – 22:00"
            );

            Map<String, Object> result = new HashMap<>();
            result.put("sessionQR",  sessionQR);
            result.put("mealType",   mealType.toString());
            result.put("date",       today);
            result.put("vendorId",   vendorId);
            result.put("timeWindow", windows.get(mealType.toString()));

            return ResponseEntity.ok(ApiResponse.success(result, "Session QR generated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

}