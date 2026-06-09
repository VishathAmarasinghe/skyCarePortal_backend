package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.AgreementReferenceMaterialDTO;
import com.skycareSolutions.skyCarePortal.dto.AgreementReferenceMaterialSummaryDTO;
import com.skycareSolutions.skyCarePortal.dto.AgreementReferenceMaterialUpdateDTO;
import com.skycareSolutions.skyCarePortal.model.AgreementInstanceReferenceMaterial;
import com.skycareSolutions.skyCarePortal.model.AgreementReferenceMaterial;
import com.skycareSolutions.skyCarePortal.repository.AgreementInstanceReferenceMaterialRepository;
import com.skycareSolutions.skyCarePortal.repository.AgreementReferenceMaterialRepository;
import com.skycareSolutions.skyCarePortal.utils.AgreementReferenceMaterialFileUtil;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AgreementReferenceMaterialService {

    @Autowired
    private AgreementReferenceMaterialRepository materialRepository;

    @Autowired
    private AgreementInstanceReferenceMaterialRepository instanceMaterialRepository;

    public List<AgreementReferenceMaterialDTO> listAll() {
        return materialRepository.findByActiveTrueOrderBySortOrderAscTitleAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<AgreementReferenceMaterialDTO> listForRecipient(String recipientType, String careGiverType) {
        String audience = "CLIENT".equalsIgnoreCase(recipientType) ? "CLIENT" : "WORKER";
        return materialRepository.findAvailableForRecipient(audience, careGiverType).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AgreementReferenceMaterial getEntity(String id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reference material not found"));
    }

    public AgreementReferenceMaterialDTO upload(
            MultipartFile file,
            String title,
            String description,
            String audience,
            String careGiverType,
            Integer sortOrder,
            String createdBy
    ) throws Exception {
        if (title == null || title.isBlank()) {
            throw new RuntimeException("Title is required");
        }
        String filePath = AgreementReferenceMaterialFileUtil.storeFile(file);
        AgreementReferenceMaterial material = new AgreementReferenceMaterial();
        material.setId(Micro_utils.idGenerator("agreementReferenceMaterial", materialRepository.getLastId()));
        material.setTitle(title.trim());
        material.setDescription(description);
        material.setFilePath(filePath);
        material.setOriginalFilename(file.getOriginalFilename());
        material.setMimeType(file.getContentType() != null
                ? file.getContentType()
                : AgreementReferenceMaterialFileUtil.guessMimeType(file.getOriginalFilename()));
        material.setAudience(normalizeAudience(audience));
        material.setCareGiverType(blankToNull(careGiverType));
        material.setSortOrder(sortOrder != null ? sortOrder : 0);
        material.setActive(true);
        material.setCreatedBy(createdBy);
        material.setCreatedAt(LocalDateTime.now());
        material.setUpdatedAt(LocalDateTime.now());
        materialRepository.save(material);
        return toDto(material);
    }

    @Transactional
    public AgreementReferenceMaterialDTO update(String id, AgreementReferenceMaterialUpdateDTO dto) {
        AgreementReferenceMaterial material = getEntity(id);
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            material.setTitle(dto.getTitle().trim());
        }
        if (dto.getDescription() != null) {
            material.setDescription(dto.getDescription());
        }
        if (dto.getAudience() != null) {
            material.setAudience(normalizeAudience(dto.getAudience()));
        }
        if (dto.getCareGiverType() != null) {
            material.setCareGiverType(blankToNull(dto.getCareGiverType()));
        }
        if (dto.getSortOrder() != null) {
            material.setSortOrder(dto.getSortOrder());
        }
        if (dto.getActive() != null) {
            material.setActive(dto.getActive());
        }
        material.setUpdatedAt(LocalDateTime.now());
        materialRepository.save(material);
        return toDto(material);
    }

    @Transactional
    public void deactivate(String id) {
        AgreementReferenceMaterial material = getEntity(id);
        material.setActive(false);
        material.setUpdatedAt(LocalDateTime.now());
        materialRepository.save(material);
    }

    public File resolveMaterialFile(String id) {
        AgreementReferenceMaterial material = getEntity(id);
        if (!Boolean.TRUE.equals(material.getActive())) {
            throw new RuntimeException("Reference material is not active");
        }
        File file = new File(material.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("Reference material file not found");
        }
        return file;
    }

    @Transactional
    public void attachToInstance(String instanceId, List<String> materialIds) {
        instanceMaterialRepository.deleteByInstanceId(instanceId);
        if (materialIds == null || materialIds.isEmpty()) {
            return;
        }

        Map<String, AgreementReferenceMaterial> unique = new LinkedHashMap<>();
        for (String materialId : materialIds) {
            if (materialId == null || materialId.isBlank()) {
                continue;
            }
            AgreementReferenceMaterial material = getEntity(materialId);
            if (!Boolean.TRUE.equals(material.getActive())) {
                throw new RuntimeException("Reference material is not active: " + material.getTitle());
            }
            unique.putIfAbsent(materialId, material);
        }

        int order = 0;
        for (AgreementReferenceMaterial material : unique.values()) {
            AgreementInstanceReferenceMaterial attached = new AgreementInstanceReferenceMaterial();
            attached.setId(Micro_utils.idGenerator(
                    "agreementInstanceReferenceMaterial",
                    instanceMaterialRepository.getLastId()));
            attached.setInstanceId(instanceId);
            attached.setSourceMaterialId(material.getId());
            attached.setTitle(material.getTitle());
            attached.setDescription(material.getDescription());
            attached.setFilePath(material.getFilePath());
            attached.setOriginalFilename(material.getOriginalFilename());
            attached.setMimeType(material.getMimeType());
            attached.setSortOrder(material.getSortOrder() != null ? material.getSortOrder() : order);
            attached.setAttachedAt(LocalDateTime.now());
            instanceMaterialRepository.save(attached);
            order++;
        }
    }

    public List<AgreementReferenceMaterialSummaryDTO> listForInstance(String instanceId) {
        return instanceMaterialRepository.findByInstanceIdOrderBySortOrderAscTitleAsc(instanceId).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    public File resolveInstanceAttachment(String instanceId, String attachmentId) {
        AgreementInstanceReferenceMaterial attachment = instanceMaterialRepository
                .findByIdAndInstanceId(attachmentId, instanceId)
                .orElseThrow(() -> new RuntimeException("Reference material attachment not found"));
        File file = new File(attachment.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("Reference material file not found");
        }
        return file;
    }

    public AgreementInstanceReferenceMaterial getInstanceAttachment(String instanceId, String attachmentId) {
        return instanceMaterialRepository.findByIdAndInstanceId(attachmentId, instanceId)
                .orElseThrow(() -> new RuntimeException("Reference material attachment not found"));
    }

    private AgreementReferenceMaterialDTO toDto(AgreementReferenceMaterial material) {
        AgreementReferenceMaterialDTO dto = new AgreementReferenceMaterialDTO();
        dto.setId(material.getId());
        dto.setTitle(material.getTitle());
        dto.setDescription(material.getDescription());
        dto.setOriginalFilename(material.getOriginalFilename());
        dto.setMimeType(material.getMimeType());
        dto.setAudience(material.getAudience());
        dto.setCareGiverType(material.getCareGiverType());
        dto.setSortOrder(material.getSortOrder() != null ? material.getSortOrder() : 0);
        dto.setActive(Boolean.TRUE.equals(material.getActive()));
        dto.setCreatedBy(material.getCreatedBy());
        dto.setCreatedAt(material.getCreatedAt());
        dto.setUpdatedAt(material.getUpdatedAt());
        return dto;
    }

    private AgreementReferenceMaterialSummaryDTO toSummary(AgreementInstanceReferenceMaterial attachment) {
        AgreementReferenceMaterialSummaryDTO dto = new AgreementReferenceMaterialSummaryDTO();
        dto.setId(attachment.getId());
        dto.setTitle(attachment.getTitle());
        dto.setDescription(attachment.getDescription());
        dto.setOriginalFilename(attachment.getOriginalFilename());
        dto.setMimeType(attachment.getMimeType());
        return dto;
    }

    private String normalizeAudience(String audience) {
        if (audience == null || audience.isBlank()) {
            return "BOTH";
        }
        String value = audience.trim().toUpperCase();
        if ("WORKER".equals(value) || "CLIENT".equals(value) || "BOTH".equals(value)) {
            return value;
        }
        return "BOTH";
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
