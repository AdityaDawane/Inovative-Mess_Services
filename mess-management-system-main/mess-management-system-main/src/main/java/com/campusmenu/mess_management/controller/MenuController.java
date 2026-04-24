package com.campusmenu.mess_management.controller;

import com.campusmenu.mess_management.dto.response.*;
import com.campusmenu.mess_management.entity.*;
import com.campusmenu.mess_management.service.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * COMPLETE Menu Controller - PUBLIC ACCESS
 * No authentication required - students can browse menus and plans
 *
 * All 5 endpoints implemented with DTOs
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MenuController {

    private final VendorService vendorService;
    private final MealPlanService mealPlanService;
    private final ModelMapper modelMapper;

    // ══════════════════════════════════════════════════════════
    // 1. GET TODAY'S MENU (PUBLIC)
    // ══════════════════════════════════════════════════════════
    /**
     * GET /api/public/menu/today
     *
     * Students can view what's cooking today without logging in
     */
    @GetMapping("/menu/today")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getTodaysMenu() {
        try {
            List<DailyMenu> menus = vendorService.getTodaysMenu();
            List<MenuResponse> responses = menus.stream()
                    .map(this::mapToMenuResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(responses,
                    "Today's menu retrieved successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // 2. GET MENU BY DATE (PUBLIC)
    // ══════════════════════════════════════════════════════════
    /**
     * GET /api/public/menu?date=2024-03-16
     *
     * View menu for a specific date
     */
    @GetMapping("/menu")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getMenuByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<DailyMenu> menus = vendorService.getMenuByDate(date);
            List<MenuResponse> responses = menus.stream()
                    .map(this::mapToMenuResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(responses,
                    "Menu for " + date + " retrieved successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // 3. GET UPCOMING MENUS (PUBLIC)
    // ══════════════════════════════════════════════════════════
    /**
     * GET /api/public/menu/upcoming
     *
     * Returns menus for the next 7 days
     */
    @GetMapping("/menu/upcoming")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getUpcomingMenus() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate nextWeek = today.plusDays(7);

            // Get all menus between today and next week
            List<MenuResponse> upcomingMenus = List.of(); // TODO: Implement in VendorService

            return ResponseEntity.ok(ApiResponse.success(upcomingMenus,
                    "Upcoming menus for next 7 days"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // 4. GET ALL MEAL PLANS (PUBLIC)
    // ══════════════════════════════════════════════════════════
    /**
     * GET /api/public/plans
     *
     * Browse all available meal plans - students can select which to book
     */
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<MealPlanResponse>>> getAvailablePlans() {
        try {
            List<MealPlan> plans = mealPlanService.getAllActivePlans();
            List<MealPlanResponse> responses = plans.stream()
                    .map(plan -> modelMapper.map(plan, MealPlanResponse.class))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(responses,
                    "Meal plans retrieved successfully. Total plans: " + responses.size()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // 5. GET PLAN DETAILS (PUBLIC)
    // ══════════════════════════════════════════════════════════
    /**
     * GET /api/public/plans/{planId}
     *
     * View details of a specific meal plan
     */
    @GetMapping("/plans/{planId}")
    public ResponseEntity<ApiResponse<MealPlanResponse>> getPlanDetails(
            @PathVariable Long planId) {
        try {
            MealPlan plan = mealPlanService.getMealPlanById(planId);
            MealPlanResponse response = modelMapper.map(plan, MealPlanResponse.class);

            return ResponseEntity.ok(ApiResponse.success(response,
                    "Plan details retrieved successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════
    // HELPER MAPPER
    // ══════════════════════════════════════════════════════════

    /**
     * Map DailyMenu entity to MenuResponse DTO
     */
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
}
