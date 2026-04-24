package com.campusmenu.mess_management.repository;

import com.campusmenu.mess_management.entity.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long>{
    Optional<SystemSettings> findBySettingKey(String settingKey);

    boolean existsBySettingKey(String settingKey);
}
