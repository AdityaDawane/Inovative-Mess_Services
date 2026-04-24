package com.campusmenu.mess_management.repository;

import com.campusmenu.mess_management.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByEmail(String email);

    Optional<Vendor> findByMachineId(String machineId);

    boolean existsByEmail(String email);

    boolean existsByMachineId(String machineId);

    @Query("SELECT v FROM Vendor v WHERE v.email = :email AND v.isActive = true")
    Optional<Vendor> findActiveByEmail(String email);

}
