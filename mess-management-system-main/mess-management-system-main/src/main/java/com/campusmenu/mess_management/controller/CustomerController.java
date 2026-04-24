package com.campusmenu.mess_management.controller;
import com.campusmenu.mess_management.enums.SubscriptionStatus;
import com.campusmenu.mess_management.repository.CustomerRepository;
import com.campusmenu.mess_management.repository.MealPlanRepository;
import com.campusmenu.mess_management.repository.SubscriptionRepository;
import com.campusmenu.mess_management.repository.VendorRepository;
import com.campusmenu.mess_management.security.JwtUtils;
import com.campusmenu.mess_management.service.CustomerService;
import jakarta.validation.Valid;
import com.campusmenu.mess_management.dto.request.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import com.campusmenu.mess_management.enums.MealType;
import com.campusmenu.mess_management.repository.AttendanceRepository;
import java.util.Base64;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Base64;

/**
 * COMPLETE Customer Controller - ALL 11 ENDPOINTS
 * Replace your existing CustomerController with this
 */
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MealPlanRepository mealPlanRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    private final CustomerService customerService;
    private final SubscriptionService subscriptionService;
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;

    // 1. REGISTER
//
//    public ResponseEntity<ApiResponse<CustomerResponse>> register(
//            @Valid @RequestBody CustomerRegistrationRequest request) {
//        try {
//            Customer customer = modelMapper.map(request, Customer.class);
//            Customer saved = customerService.registerCustomer(customer);
//            CustomerResponse response = modelMapper.map(saved, CustomerResponse.class);
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(ApiResponse.success(response, "Registration successful!"));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerResponse>> register(
            @Valid @RequestBody CustomerRegistrationRequest request) {
        try {
            // ✅ Manual mapping - fixes password field
            Customer customer = new Customer();
            customer.setFullName(request.getFullName());
            customer.setEmail(request.getEmail());
            customer.setPhone(request.getPhone());
            customer.setPasswordHash(request.getPassword());  // ✅ KEY LINE!

            Customer saved = customerService.registerCustomer(customer);
            CustomerResponse response = modelMapper.map(saved, CustomerResponse.class);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Registration successful!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 2. LOGIN
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        try {
            Customer customer = customerService
                    .loginCustomer(request.getEmail(), request.getPassword())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            String token = jwtUtils.generateToken(
                    customer.getEmail(), customer.getCustomerId(), "CUSTOMER");

            LoginResponse response = LoginResponse.builder()
                    .token(token).tokenType("Bearer")
                    .userId(customer.getCustomerId())
                    .fullName(customer.getFullName())
                    .email(customer.getEmail())
                    .role("CUSTOMER")
                    .message("Login successful!")
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response, "Welcome back!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials"));
        }
    }

    // 3. GET PROFILE
    @GetMapping("/profile/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getProfile(
            @PathVariable Long customerId) {
        try {
            Customer customer = customerService.getCustomerById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            return ResponseEntity.ok(ApiResponse.success(
                    modelMapper.map(customer, CustomerResponse.class), "Profile retrieved"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 4. UPDATE PROFILE
    @PutMapping("/profile/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateProfile(
            @PathVariable Long customerId,
            @RequestBody Customer updated) {
        try {
            Customer customer = customerService.updateCustomerProfile(customerId, updated);
            return ResponseEntity.ok(ApiResponse.success(
                    modelMapper.map(customer, CustomerResponse.class), "Profile updated"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 5. BOOK MEAL
    @PostMapping("/book-meal")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> bookMeal(
            @Valid @RequestBody BookMealRequest request) {
        try {
            Subscription sub = subscriptionService.bookMealPlan(
                    request.getCustomerId(), request.getPlanId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(toSubResponse(sub),
                            "Meal booked! Complete payment to activate."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 6. CANCEL BOOKING
    @PostMapping("/cancel-booking/{subscriptionId}")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> cancelBooking(
            @PathVariable Long subscriptionId) {
        try {
            Subscription sub = subscriptionService.cancelSubscription(subscriptionId);
            return ResponseEntity.ok(ApiResponse.success(toSubResponse(sub), "Cancelled"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 7. ACTIVE BOOKINGS
    @GetMapping("/active-bookings/{customerId}")
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getActiveBookings(
            @PathVariable Long customerId) {
        try {
            List<SubscriptionResponse> list = customerService.getActiveSubscriptions(customerId)
                    .stream().map(this::toSubResponse).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(list, "Active bookings retrieved"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 8. BOOKING HISTORY
    @GetMapping("/booking-history/{customerId}")
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getBookingHistory(
            @PathVariable Long customerId) {
        try {
            List<SubscriptionResponse> list = customerService.getBookingHistory(customerId)
                    .stream().map(this::toSubResponse).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(list, "Booking history retrieved"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 9. GET QR CODE
    @GetMapping("/qr-code/{customerId}")
    public ResponseEntity<ApiResponse<String>> getQRCode(@PathVariable Long customerId) {
        try {
            String qr = customerService.getActiveQRCode(customerId);
            return ResponseEntity.ok(ApiResponse.success(qr, "Show this QR at mess"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 10. ATTENDANCE HISTORY
    @GetMapping("/attendance-history/{customerId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getAttendanceHistory(
            @PathVariable Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            if (startDate == null) startDate = LocalDate.now().minusDays(30);
            if (endDate == null) endDate = LocalDate.now();

            List<AttendanceResponse> list = customerService
                    .getAttendanceHistory(customerId, startDate, endDate)
                    .stream().map(this::toAttendanceResponse).collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(list, "Attendance retrieved"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 11. PAYMENT HISTORY
    @GetMapping("/payment-history/{customerId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentHistory(
            @PathVariable Long customerId) {
        try {
            List<PaymentResponse> list = customerService.getPaymentHistory(customerId)
                    .stream().map(this::toPaymentResponse).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(list, "Payment history retrieved"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> subscribeToPlan(
            @RequestBody Map<String, Object> request) {
        try {
            // FIXED ✅
            Long customerId = Long.parseLong(String.valueOf(request.get("customerId")));
            Long planId     = Long.parseLong(String.valueOf(request.get("planId")));
            Long vendorId   = Long.parseLong(String.valueOf(request.get("vendorId")));
            String paymentId = (String) request.get("paymentId");
            String paymentOrderId = (String) request.get("paymentOrderId");

            // ✅ Check if already subscribed to this vendor
            boolean samePlanActive = subscriptionRepository
                    .existsActiveSamePlanByCustomer(customerId, planId);

            if (samePlanActive) {
                return ResponseEntity.badRequest().body(ApiResponse.error(
                        "You already have an active or pending subscription for this plan. " +
                                "Please wait for it to expire before purchasing it again."));
            }

            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            MealPlan plan = mealPlanRepository.findById(planId)
                    .orElseThrow(() -> new RuntimeException("Meal plan not found"));

            Vendor vendor = vendorRepository.findById(vendorId)
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            // Create subscription in PENDING status
            Subscription subscription = new Subscription();
            subscription.setCustomer(customer);
            subscription.setMealPlan(plan);
            subscription.setVendor(vendor);
            subscription.setStatus(SubscriptionStatus.PENDING);
            subscription.setStartDate(LocalDate.now());
            subscription.setEndDate(LocalDate.now().plusDays(plan.getDurationDays()));
            subscription.setTotalAmount(plan.getFinalPrice());
            subscription.setPaymentId(paymentId);
            subscription.setPaymentOrderId(paymentOrderId);
            subscription.setPaymentDate(LocalDateTime.now());
            subscription.setIsApproved(false);
            subscription.setCreatedAt(LocalDateTime.now());
            subscription.setUpdatedAt(LocalDateTime.now());

            Subscription saved = subscriptionRepository.save(subscription);

            // ✅ FIXED: Remove 'message' field (not in SubscriptionResponse)
            SubscriptionResponse response = SubscriptionResponse.builder()
                    .subscriptionId(saved.getSubscriptionId())
                    .customerId(customerId)
                    .planId(planId)
                    .status(saved.getStatus())
                    .totalAmount(saved.getTotalAmount())
                    // Removed .message() - put it in ApiResponse instead
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response,
                    "Subscription created! Payment received. Waiting for vendor approval."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    // ── 2. Student taps "Mark Attendance" → sends session QR ───────────
//    @PostMapping("/mark-attendance")
//    public ResponseEntity<ApiResponse<Map<String, Object>>> markAttendance(
//            @RequestBody Map<String, Object> request) {
//        try {
//            Long customerId = ((Number) request.get("customerId")).longValue();
//            String sessionQR = (String) request.get("sessionQR");
//            String mealTypeStr = (String) request.get("mealType");
//            MealType mealType = MealType.valueOf(mealTypeStr);
//
//            // Decode session QR → vendorId + mealType + date
//            String decoded = new String(Base64.getDecoder().decode(sessionQR));
//            // Format: SESSION-{vendorId}-{mealType}-{date}
//            String[] parts = decoded.split("-");
//            if (parts.length < 4 || !parts[0].equals("SESSION")) {
//                return ResponseEntity.badRequest().body(ApiResponse.error("Invalid session QR code"));
//            }
//            Long vendorId  = Long.parseLong(parts[1]);
//            String qrMeal  = parts[2];
//            String qrDate  = parts[3];
//
//            // Validate date — QR must be for today
//            if (!qrDate.equals(LocalDate.now().toString())) {
//                return ResponseEntity.badRequest().body(ApiResponse.error("This QR code has expired. Ask vendor for today's code."));
//            }
//
//            // Validate meal type matches
//            if (!qrMeal.equals(mealTypeStr)) {
//                return ResponseEntity.badRequest().body(ApiResponse.error("Wrong meal QR. This is for " + qrMeal));
//            }
//
//            // Find student's active subscription with this vendor
//            Subscription subscription = subscriptionRepository
//                    .findByCustomerIdAndVendorIdAndStatus(customerId, vendorId, SubscriptionStatus.ACTIVE)
//                    .orElseThrow(() -> new RuntimeException("No active subscription with this mess"));
//
//            // Check meal included in plan
//            Boolean included = subscription.getMealPlan()
//                    .getMealsIncluded().get(mealType.name().toLowerCase());
//            if (included == null || !included) {
//                return ResponseEntity.badRequest()
//                        .body(ApiResponse.error(mealType + " is not included in your plan"));
//            }
//
//            // Check already scanned today
//            if (attendanceRepository.existsByCustomer_CustomerIdAndMealTypeAndScanDate(
//                    customerId, mealType, LocalDate.now())) {
//                return ResponseEntity.badRequest()
//                        .body(ApiResponse.error("You already marked attendance for " + mealType + " today"));
//            }
//
//            // Get vendor & customer
//            Customer customer = customerRepository.findById(customerId)
//                    .orElseThrow(() -> new RuntimeException("Customer not found"));
//            Vendor vendor = vendorRepository.findById(vendorId)
//                    .orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//            // Save attendance
//            Attendance attendance = Attendance.builder()
//                    .subscription(subscription)
//                    .customer(customer)
//                    .mealType(mealType)
//                    .scanDate(LocalDate.now())
//                    .scanTime(LocalDateTime.now())
//                    .scannedByVendor(vendor)
//                    .machineId("STUDENT-APP")
//                    .isValid(true)
//                    .build();
//            attendanceRepository.save(attendance);
//
//            Map<String, Object> resp = new HashMap<>();
//            resp.put("customerName", customer.getFullName());
//            resp.put("mealType", mealType.toString());
//            resp.put("scanTime", LocalDateTime.now().toString());
//            resp.put("messName", vendor.getMessName());
//
//            return ResponseEntity.ok(ApiResponse.success(resp, "✅ Attendance marked! Enjoy your " + mealType.toString().toLowerCase() + "!"));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }
    @PostMapping("/mark-attendance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markAttendance(
            @RequestBody Map<String, Object> request) {
        try {
            Long customerId    = ((Number) request.get("customerId")).longValue();
            String sessionQR   = (String) request.get("sessionQR");
            String mealTypeStr = (String) request.get("mealType");

            // Decode session QR → SESSION-{vendorId}-{mealType}-{date}
            String decoded = new String(Base64.getDecoder().decode(sessionQR));
            String[] parts = decoded.split("-");
            if (parts.length < 4 || !parts[0].equals("SESSION")) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Invalid session QR code"));
            }
            Long   vendorId = Long.parseLong(parts[1]);
            String qrMeal   = parts[2];
            String qrDate   = parts[3];

            // Must be today's QR
            if (!qrDate.equals(LocalDate.now().toString())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("QR code has expired"));
            }

            // Meal type must match
            if (!qrMeal.equals(mealTypeStr)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Wrong meal QR. This is for " + qrMeal));
            }

            // Parse meal type enum
            MealType mealType = MealType.valueOf(mealTypeStr);

            // Find student's active subscription with this vendor
            Subscription subscription = subscriptionRepository
                    .findByCustomerIdAndVendorIdAndStatus(customerId, vendorId, SubscriptionStatus.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("No active subscription with this mess"));

            // Check meal is included in plan
            Boolean included = subscription.getMealPlan()
                    .getMealsIncluded().get(mealType.name().toLowerCase());
            if (included == null || !included) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(mealType.name() + " is not included in your plan"));
            }

            // Check already scanned today
            if (attendanceRepository.existsByCustomer_CustomerIdAndMealTypeAndScanDate(
                    customerId, mealType, LocalDate.now())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Already marked attendance for " + mealType.name() + " today"));
            }

            // Fetch customer and vendor
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            Vendor vendor = vendorRepository.findById(vendorId)
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            // Save attendance
            Attendance attendance = Attendance.builder()
                    .subscription(subscription)
                    .customer(customer)
                    .mealType(mealType)
                    .scanDate(LocalDate.now())
                    .scanTime(LocalDateTime.now())
                    .scannedByVendor(vendor)
                    .machineId("STUDENT-APP")
                    .isValid(true)
                    .build();
            attendanceRepository.save(attendance);

            Map<String, Object> resp = new HashMap<>();
            resp.put("customerName", customer.getFullName());
            resp.put("mealType",     mealType.name());
            resp.put("scanTime",     LocalDateTime.now().toString());
            resp.put("messName",     vendor.getMessName());

            return ResponseEntity.ok(ApiResponse.success(resp,
                    "Attendance marked! Enjoy your " + mealType.name().toLowerCase() + "!"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }


    // MAPPERS
    private SubscriptionResponse toSubResponse(Subscription s) {
        return SubscriptionResponse.builder()
                .subscriptionId(s.getSubscriptionId())
                .customerId(s.getCustomer().getCustomerId())
                .customerName(s.getCustomer().getFullName())
                .planId(s.getMealPlan().getPlanId())
                .planName(s.getMealPlan().getPlanName())
                .qrCode(s.getQrCode())
                .startDate(s.getStartDate())
                .endDate(s.getEndDate())
                .status(s.getStatus())
                .totalAmount(s.getTotalAmount())
                .daysRemaining(s.getDaysRemaining())
                .build();
    }

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

    private AttendanceResponse toAttendanceResponse(Attendance a) {
        return AttendanceResponse.builder()
                .attendanceId(a.getAttendanceId())
                .customerId(a.getCustomer().getCustomerId())
                .customerName(a.getCustomer().getFullName())
                .mealType(a.getMealType())
                .scanDate(a.getScanDate())
                .scanTime(a.getScanTime())
                .vendorName(a.getScannedByVendor().getFullName())
                .messName(a.getScannedByVendor().getMessName())
                .isValid(a.getIsValid())
                .message("Meal served")
                .build();
    }
}
