package com.campusmenu.mess_management.service;

import com.campusmenu.mess_management.entity.Admin;
import com.campusmenu.mess_management.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * ADMIN SERVICE
 * Handles admin authentication and operations
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Admin login
     * Returns admin if credentials are valid
     */
    public Optional<Admin> loginAdmin(String email, String password) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);

        if (adminOpt.isEmpty()) {
            return Optional.empty();
        }

        Admin admin = adminOpt.get();

        // Check if password matches
        if (passwordEncoder.matches(password, admin.getPasswordHash())) {
            return Optional.of(admin);
        }

        return Optional.empty();
    }

    /**
     * Get admin by ID
     */
    public Optional<Admin> getAdminById(Long adminId) {
        return adminRepository.findById(adminId);
    }

    /**
     * Register new admin (for initial setup)
     */
    public Admin registerAdmin(Admin admin) {
        // Check if email already exists
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("Admin with this email already exists");
        }

        // Encrypt password
        admin.setPasswordHash(passwordEncoder.encode(admin.getPasswordHash()));

        return adminRepository.save(admin);
    }
}
