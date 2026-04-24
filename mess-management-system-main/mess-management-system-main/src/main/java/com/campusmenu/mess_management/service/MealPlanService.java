package com.campusmenu.mess_management.service;
import com.campusmenu.mess_management.entity.MealPlan;
import com.campusmenu.mess_management.enums.PlanType;
import com.campusmenu.mess_management.repository.MealPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class MealPlanService {
    private final MealPlanRepository mealPlanRepository;

    /**
     * Create new meal plan
     */
    public MealPlan createMealPlan(MealPlan mealPlan) {
        // Calculate final price
        mealPlan.calculateFinalPrice();
        return mealPlanRepository.save(mealPlan);
    }

    /**
     * Update meal plan
     */
    public MealPlan updateMealPlan(Long planId, MealPlan updatedPlan) {
        MealPlan mealPlan = mealPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));

        mealPlan.setPlanName(updatedPlan.getPlanName());
        mealPlan.setPlanType(updatedPlan.getPlanType());
        mealPlan.setDurationDays(updatedPlan.getDurationDays());
        mealPlan.setPrice(updatedPlan.getPrice());
        mealPlan.setDiscountPercentage(updatedPlan.getDiscountPercentage());
        mealPlan.setMealsIncluded(updatedPlan.getMealsIncluded());
        mealPlan.setDescription(updatedPlan.getDescription());
        mealPlan.setIsActive(updatedPlan.getIsActive());

        // Recalculate final price
        mealPlan.calculateFinalPrice();

        return mealPlanRepository.save(mealPlan);
    }

    /**
     * Delete meal plan
     */
    public void deleteMealPlan(Long planId) {
        mealPlanRepository.deleteById(planId);
    }

    /**
     * Get meal plan by ID
     */
    @Transactional(readOnly = true)
    public MealPlan getMealPlanById(Long planId) {
        return mealPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));
    }

    /**
     * Get all active meal plans
     */
    @Transactional(readOnly = true)
    public List<MealPlan> getAllActivePlans() {
        return mealPlanRepository.findAllActivePlans();
    }

    /**
     * Get all meal plans
     */
    @Transactional(readOnly = true)
    public List<MealPlan> getAllPlans() {
        return mealPlanRepository.findAll();
    }

    /**
     * Get plans by type
     */
    @Transactional(readOnly = true)
    public List<MealPlan> getPlansByType(PlanType planType) {
        return mealPlanRepository.findActivePlansByType(planType);
    }

    /**
     * Activate/Deactivate plan
     */
    public MealPlan togglePlanStatus(Long planId) {
        MealPlan mealPlan = mealPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));

        mealPlan.setIsActive(!mealPlan.getIsActive());
        return mealPlanRepository.save(mealPlan);
    }
}
