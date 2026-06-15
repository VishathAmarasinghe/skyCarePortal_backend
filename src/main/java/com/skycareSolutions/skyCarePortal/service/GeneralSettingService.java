package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.GeneralSettingDTO;
import com.skycareSolutions.skyCarePortal.model.GeneralSetting;
import com.skycareSolutions.skyCarePortal.repository.GeneralSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GeneralSettingService {

    @Autowired
    private GeneralSettingRepository repository;

    public GeneralSettingDTO get() {
        return toDto(ensureExists());
    }

    public boolean isBlockTimesheetOnDocumentNonCompliance() {
        return ensureExists().isBlockTimesheetOnDocumentNonCompliance();
    }

    public GeneralSettingDTO update(GeneralSettingDTO input) {
        GeneralSetting setting = ensureExists();
        setting.setBlockTimesheetOnDocumentNonCompliance(input.isBlockTimesheetOnDocumentNonCompliance());
        setting.setUpdatedAt(LocalDateTime.now());
        return toDto(repository.save(setting));
    }

    private GeneralSetting ensureExists() {
        return repository.findById(GeneralSetting.SINGLETON_ID).orElseGet(() -> {
            GeneralSetting created = new GeneralSetting();
            created.setId(GeneralSetting.SINGLETON_ID);
            created.setBlockTimesheetOnDocumentNonCompliance(false);
            created.setUpdatedAt(LocalDateTime.now());
            return repository.save(created);
        });
    }

    private GeneralSettingDTO toDto(GeneralSetting entity) {
        GeneralSettingDTO dto = new GeneralSettingDTO();
        dto.setBlockTimesheetOnDocumentNonCompliance(entity.isBlockTimesheetOnDocumentNonCompliance());
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
