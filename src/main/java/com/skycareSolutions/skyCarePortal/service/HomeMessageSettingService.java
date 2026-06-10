package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.HomeMessageSettingDTO;
import com.skycareSolutions.skyCarePortal.model.HomeMessageSetting;
import com.skycareSolutions.skyCarePortal.repository.HomeMessageSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HomeMessageSettingService {

    private static final String SETTING_ID = "HOME_MESSAGE";

    @Autowired
    private HomeMessageSettingRepository repository;

    private HomeMessageSettingDTO toDTO(HomeMessageSetting entity) {
        HomeMessageSettingDTO dto = new HomeMessageSettingDTO();
        dto.setEnabled(entity.isEnabled());
        dto.setMessage(entity.getMessage());
        dto.setType(entity.getType());
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }

    private HomeMessageSetting ensureExists() {
        return repository.findById(SETTING_ID).orElseGet(() -> {
            HomeMessageSetting created = new HomeMessageSetting();
            created.setId(SETTING_ID);
            created.setEnabled(false);
            created.setMessage(null);
            created.setType("GENERAL");
            created.setUpdatedAt(LocalDateTime.now());
            return repository.save(created);
        });
    }

    public HomeMessageSettingDTO get() {
        return toDTO(ensureExists());
    }

    public HomeMessageSettingDTO update(HomeMessageSettingDTO input) {
        HomeMessageSetting setting = ensureExists();
        setting.setEnabled(input.isEnabled());
        setting.setMessage(input.getMessage());
        setting.setType(input.getType() == null || input.getType().trim().isEmpty() ? "GENERAL" : input.getType().trim());
        setting.setUpdatedAt(LocalDateTime.now());
        return toDTO(repository.save(setting));
    }
}

