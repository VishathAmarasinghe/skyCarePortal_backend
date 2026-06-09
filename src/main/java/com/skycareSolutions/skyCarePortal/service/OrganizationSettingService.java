package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.OrganizationSettingDTO;
import com.skycareSolutions.skyCarePortal.model.OrganizationSetting;
import com.skycareSolutions.skyCarePortal.repository.OrganizationSettingRepository;
import com.skycareSolutions.skyCarePortal.utils.AgreementAssetFileUtil;
import com.skycareSolutions.skyCarePortal.utils.AgreementLogoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class OrganizationSettingService {

    @Autowired
    private OrganizationSettingRepository repository;

    @Value("${app.external.service.baseUrl:http://localhost:5000}")
    private String backendBaseUrl;

    public OrganizationSettingDTO get() {
        return toDto(ensureExists());
    }

    public OrganizationSetting getEntity() {
        return ensureExists();
    }

    public OrganizationSettingDTO update(OrganizationSettingDTO input) {
        OrganizationSetting entity = ensureExists();
        entity.setName(input.getName());
        entity.setAbn(input.getAbn());
        entity.setAddress(input.getAddress());
        entity.setPhone(input.getPhone());
        entity.setEmail(input.getEmail());
        entity.setDirectorName(input.getDirectorName());
        entity.setUpdatedAt(LocalDateTime.now());
        return toDto(repository.save(entity));
    }

    public OrganizationSettingDTO uploadLogo(MultipartFile file) throws Exception {
        OrganizationSetting entity = ensureExists();
        String logoPath = AgreementAssetFileUtil.storeOrgLogo(file);
        entity.setLogoPath(logoPath);
        entity.setUpdatedAt(LocalDateTime.now());
        return toDto(repository.save(entity));
    }

    public String buildLogoUrl(OrganizationSetting entity) {
        if (entity.getLogoPath() == null || entity.getLogoPath().isBlank()) {
            return null;
        }
        return backendBaseUrl + "/settings/organization/logo";
    }

    /** NDIS banner URL — only for client service agreement documents. */
    public String buildClientServiceAgreementLogoUrl() {
        return backendBaseUrl + AgreementLogoUtil.DEFAULT_LOGO_PATH;
    }

    private OrganizationSetting ensureExists() {
        return repository.findById(OrganizationSetting.SINGLETON_ID).orElseGet(() -> {
            OrganizationSetting defaults = new OrganizationSetting();
            defaults.setId(OrganizationSetting.SINGLETON_ID);
            defaults.setName("SkyCare Solutions Pty Ltd");
            defaults.setAbn("");
            defaults.setAddress("Geelong, VIC");
            defaults.setPhone("");
            defaults.setEmail("info@skycaresolutions.com.au");
            defaults.setDirectorName("Director, SkyCare Solutions Pty Ltd");
            defaults.setUpdatedAt(LocalDateTime.now());
            return repository.save(defaults);
        });
    }

    private OrganizationSettingDTO toDto(OrganizationSetting entity) {
        OrganizationSettingDTO dto = new OrganizationSettingDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAbn(entity.getAbn());
        dto.setAddress(entity.getAddress());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setDirectorName(entity.getDirectorName());
        dto.setLogoPath(entity.getLogoPath());
        dto.setLogoUrl(buildLogoUrl(entity));
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
