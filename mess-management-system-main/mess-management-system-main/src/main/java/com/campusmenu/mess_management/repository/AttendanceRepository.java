package com.campusmenu.mess_management.repository;

import com.campusmenu.mess_management.entity.Attendance;
import com.campusmenu.mess_management.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository

public interface AttendanceRepository extends JpaRepository<Attendance, Long>{

    List<Attendance> findByCustomer_CustomerId(Long customerId);

    List<Attendance> findByScanDate(LocalDate scanDate);

    Optional<Attendance> findByCustomer_CustomerIdAndMealTypeAndScanDate(
            Long customerId,
            MealType mealType,
            LocalDate scanDate
    );

    @Query("SELECT a FROM Attendance a WHERE a.customer.customerId = :customerId " +
            "AND a.scanDate BETWEEN :startDate AND :endDate " +
            "ORDER BY a.scanDate DESC, a.scanTime DESC")
    List<Attendance> findAttendanceHistory(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT a FROM Attendance a WHERE a.scanDate = CURRENT_DATE " +
            "ORDER BY a.scanTime DESC")
    List<Attendance> findTodaysAttendance();

    @Query("SELECT a FROM Attendance a WHERE a.scannedByVendor.vendorId = :vendorId " +
            "AND a.scanDate = :date ORDER BY a.scanTime DESC")
    List<Attendance> findAttendanceByVendorAndDate(
            @Param("vendorId") Long vendorId,
            @Param("date") LocalDate date
    );

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.scanDate = :date " +
            "AND a.mealType = :mealType")
    Long countAttendanceByDateAndMealType(
            @Param("date") LocalDate date,
            @Param("mealType") MealType mealType
    );

    @Query("SELECT COUNT(DISTINCT a.customer.customerId) FROM Attendance a " +
            "WHERE a.scanDate = :date AND a.mealType = :mealType")
    Long countUniqueCustomersByDateAndMealType(
            @Param("date") LocalDate date,
            @Param("mealType") MealType mealType
    );

    boolean existsByCustomer_CustomerIdAndMealTypeAndScanDate(
            Long customerId,
            MealType mealType,
            LocalDate scanDate
    );
}
