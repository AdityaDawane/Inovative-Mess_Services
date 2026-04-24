package com.campusmenu.mess_management.controller;

import com.campusmenu.mess_management.dto.request.PaymentRequest;
import com.campusmenu.mess_management.dto.response.*;
import com.campusmenu.mess_management.entity.Payment;
import com.campusmenu.mess_management.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * PAYMENT CONTROLLER - FIXED VERSION
 * Now uses correct PaymentService method signatures
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${razorpay.key.id:rzp_test_YOUR_KEY_ID}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:YOUR_KEY_SECRET}")
    private String razorpayKeySecret;

    // ══════════════════════════════════════════════════════════
    // 1. CREATE ORDER
    // ══════════════════════════════════════════════════════════

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrder(
            @Valid @RequestBody PaymentRequest request) {
        try {
            // Create payment record
            Payment payment = paymentService.createPayment(
                    request.getSubscriptionId(),
                    request.getPaymentMethod(),
                    request.getPaymentGateway()
            );

            // Build Razorpay order details
            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("keyId", razorpayKeyId);
            orderDetails.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).intValue());
            orderDetails.put("currency", "INR");
            orderDetails.put("paymentId", payment.getPaymentId()); // ✅ Return paymentId (Long)
            orderDetails.put("transactionId", payment.getTransactionId());
            orderDetails.put("subscriptionId", request.getSubscriptionId());
            orderDetails.put("description", "Mess Management - Meal Plan");

            return ResponseEntity.ok(ApiResponse.success(orderDetails, "Order created"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed: " + e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // 2. VERIFY PAYMENT
    // ══════════════════════════════════════════════════════════

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(
            @RequestBody Map<String, Object> data) {

        String razorpayPaymentId = (String) data.get("razorpay_payment_id");
        String razorpayOrderId = (String) data.get("razorpay_order_id");
        String razorpaySignature = (String) data.get("razorpay_signature");
        Long paymentId = Long.valueOf(data.get("paymentId").toString()); // ✅ Use paymentId (Long)

        try {
            // Verify signature
            boolean isValid = verifyRazorpaySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);

            if (!isValid) {
                paymentService.markPaymentFailed(paymentId); // ✅ Pass Long
                return ResponseEntity.badRequest().body(ApiResponse.error("Signature mismatch"));
            }

            // Mark success and activate subscription
            Payment payment = paymentService.markPaymentSuccess(paymentId, razorpayPaymentId); // ✅ Pass Long

            PaymentResponse response = PaymentResponse.builder()
                    .paymentId(payment.getPaymentId())
                    .subscriptionId(payment.getSubscription().getSubscriptionId())
                    .customerId(payment.getCustomer().getCustomerId())
                    .customerName(payment.getCustomer().getFullName())
                    .amount(payment.getAmount())
                    .paymentMethod(payment.getPaymentMethod())
                    .transactionId(payment.getTransactionId())
                    .gatewayPaymentId(razorpayPaymentId)
                    .status(payment.getStatus())
                    .paymentDate(payment.getPaymentDate())
                    .message("Payment successful! Subscription activated.")
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response, "Payment verified ✅"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // 3. HANDLE FAILURE
    // ══════════════════════════════════════════════════════════

    @PostMapping("/failed")
    public ResponseEntity<ApiResponse<Void>> paymentFailed(@RequestBody Map<String, Object> data) {
        try {
            Long paymentId = Long.valueOf(data.get("paymentId").toString()); // ✅ Use paymentId (Long)
            paymentService.markPaymentFailed(paymentId); // ✅ Pass Long
            return ResponseEntity.ok(ApiResponse.success("Payment marked as failed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // 4. CHECK STATUS
    // ══════════════════════════════════════════════════════════

    @GetMapping("/status/{transactionId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getStatus(@PathVariable String transactionId) {
        try {
            Payment payment = paymentService.getPaymentByTransactionId(transactionId);
            PaymentResponse response = PaymentResponse.builder()
                    .paymentId(payment.getPaymentId())
                    .amount(payment.getAmount())
                    .transactionId(payment.getTransactionId())
                    .status(payment.getStatus())
                    .paymentDate(payment.getPaymentDate())
                    .message("Status: " + payment.getStatus())
                    .build();
            return ResponseEntity.ok(ApiResponse.success(response, "Status retrieved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // SECURITY: SIGNATURE VERIFICATION
    // ══════════════════════════════════════════════════════════

    private boolean verifyRazorpaySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String generated = HexFormat.of().formatHex(hash);
            return generated.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}
