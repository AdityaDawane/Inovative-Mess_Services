package com.campusmenu.mess_management.repository;
import com.campusmenu.mess_management.entity.DailyMenu;
import com.campusmenu.mess_management.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository

public interface DailyMenuRepository extends JpaRepository<DailyMenu, Long>{
    List<DailyMenu> findByMenuDate(LocalDate menuDate);

    Optional<DailyMenu> findByMenuDateAndMealType(LocalDate menuDate, MealType mealType);

    List<DailyMenu> findByVendor_VendorId(Long vendorId);

    @Query("SELECT d FROM DailyMenu d WHERE d.menuDate = :date " +
            "AND d.isAvailable = true ORDER BY d.mealType")
    List<DailyMenu> findAvailableMenuByDate(@Param("date") LocalDate date);

    @Query("SELECT d FROM DailyMenu d WHERE d.menuDate = CURRENT_DATE " +
            "AND d.isAvailable = true ORDER BY d.mealType")
    List<DailyMenu> findTodaysMenu();

    @Query("SELECT d FROM DailyMenu d WHERE d.menuDate >= CURRENT_DATE " +
            "ORDER BY d.menuDate, d.mealType")
    List<DailyMenu> findUpcomingMenus();

    @Query("SELECT d FROM DailyMenu d WHERE d.vendor.vendorId = :vendorId " +
            "AND d.menuDate BETWEEN :startDate AND :endDate " +
            "ORDER BY d.menuDate, d.mealType")
    List<DailyMenu> findMenusByVendorAndDateRange(
            @Param("vendorId") Long vendorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByMenuDateAndMealType(LocalDate menuDate, MealType mealType);
}
