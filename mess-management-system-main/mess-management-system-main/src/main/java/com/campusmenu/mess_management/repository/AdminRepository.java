package com.campusmenu.mess_management.repository;


import com.campusmenu.mess_management.entity.Admin;
import com.campusmenu.mess_management.enums.AdminRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface AdminRepository  extends JpaRepository<Admin, Long>{
    Optional<Admin> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Admin> findByRole(AdminRole role);

    @Query("SELECT a FROM Admin a WHERE a.email = :email AND a.isActive = true")
    Optional<Admin> findActiveByEmail(String email);
}
