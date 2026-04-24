package com.campusmenu.mess_management.repository;
import com.campusmenu.mess_management.entity.MealPlan;
import com.campusmenu.mess_management.enums.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface MealPlanRepository extends JpaRepository<MealPlan, Long>{




    List<MealPlan> findByIsActive(Boolean isActive);

    List<MealPlan> findByPlanType(PlanType planType);

    // ADD THESE TWO MISSING METHODS:
    @Query("SELECT m FROM MealPlan m WHERE m.isActive = true ORDER BY m.finalPrice ASC")
    List<MealPlan> findAllActivePlans();

    @Query("SELECT m FROM MealPlan m WHERE m.isActive = true AND m.planType = :planType")
    List<MealPlan> findActivePlansByType(PlanType planType);}


