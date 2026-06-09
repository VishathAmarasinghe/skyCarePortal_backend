package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.*;
import com.skycareSolutions.skyCarePortal.utils.AgreementAssetFileUtil;
import com.skycareSolutions.skyCarePortal.utils.HtmlSanitizerUtil;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgreementTemplateService {

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_PUBLISHED = "PUBLISHED";
    public static final String STATUS_ARCHIVED = "ARCHIVED";

    @Autowired
    private AgreementTemplateRepository templateRepository;

    @Autowired
    private AgreementTemplateVersionRepository versionRepository;

    @Autowired
    private AgreementTemplateAssetRepository assetRepository;

    @Autowired
    private AgreementMergeService mergeService;

    public List<AgreementTemplateDTO> getAllTemplates() {
        return templateRepository.findAll().stream()
                .filter(this::isVisibleTemplate)
                .map(this::toTemplateDto)
                .collect(Collectors.toList());
    }

    private boolean isVisibleTemplate(AgreementTemplate template) {
        if (template == null) {
            return false;
        }
        if (AgreementSeedContent.RETIRED_WORKER_TEMPLATE_KEY.equals(template.getTemplateKey())) {
            return false;
        }
        return template.getStatus() == null || !"Inactive".equalsIgnoreCase(template.getStatus());
    }

    public AgreementTemplateDTO getTemplateById(String id) {
        AgreementTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agreement template not found"));
        return toTemplateDto(template);
    }

    @Transactional
    public AgreementTemplateDTO createTemplate(AgreementTemplateCreateDTO dto) {
        if (dto.getTemplateKey() == null || dto.getTemplateKey().isBlank()) {
            throw new RuntimeException("templateKey is required");
        }
        if (templateRepository.findByTemplateKey(dto.getTemplateKey()).isPresent()) {
            throw new RuntimeException("Template key already exists");
        }

        String templateId = Micro_utils.idGenerator("agreementTemplate", templateRepository.getLastId());
        LocalDateTime now = LocalDateTime.now();

        AgreementTemplate template = new AgreementTemplate();
        template.setId(templateId);
        template.setTemplateKey(dto.getTemplateKey().trim());
        template.setDisplayName(dto.getDisplayName());
        template.setAudience(dto.getAudience());
        template.setCareGiverType(dto.getCareGiverType());
        template.setDefaultForAudience(dto.isDefaultForAudience());
        template.setStatus("Active");
        template.setCreatedAt(now);
        template.setUpdatedAt(now);
        template = templateRepository.save(template);

        createDraftVersion(
                template,
                dto.getInitialContentHtml() != null ? dto.getInitialContentHtml() : "<p>New agreement template</p>",
                dto.getChangeNotes() != null ? dto.getChangeNotes() : "Initial draft"
        );

        return toTemplateDto(template);
    }

    public AgreementTemplateDTO updateTemplate(String id, AgreementTemplateUpdateDTO dto) {
        AgreementTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agreement template not found"));
        if (dto.getDisplayName() != null) {
            template.setDisplayName(dto.getDisplayName());
        }
        if (dto.getAudience() != null) {
            template.setAudience(dto.getAudience());
        }
        if (dto.getCareGiverType() != null) {
            template.setCareGiverType(dto.getCareGiverType());
        }
        if (dto.getDefaultForAudience() != null) {
            template.setDefaultForAudience(dto.getDefaultForAudience());
        }
        if (dto.getStatus() != null) {
            template.setStatus(dto.getStatus());
        }
        template.setUpdatedAt(LocalDateTime.now());
        return toTemplateDto(templateRepository.save(template));
    }

    public List<AgreementTemplateVersionSummaryDTO> getVersions(String templateId) {
        ensureTemplate(templateId);
        return versionRepository.findByTemplate_IdOrderByVersionNumberDesc(templateId).stream()
                .map(this::toVersionSummary)
                .collect(Collectors.toList());
    }

    public AgreementTemplateVersionDTO getVersion(String versionId) {
        AgreementTemplateVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Template version not found"));
        return toVersionDto(version);
    }

    @Transactional
    public AgreementTemplateVersionDTO createDraftVersion(String templateId, String changeNotes) {
        AgreementTemplate template = ensureTemplate(templateId);
        Integer maxVersion = versionRepository.findMaxVersionNumber(templateId);
        int nextVersion = maxVersion != null ? maxVersion + 1 : 1;

        AgreementTemplateVersion published = versionRepository
                .findByTemplate_IdAndStatus(templateId, STATUS_PUBLISHED)
                .orElse(null);

        String baseContent = published != null ? published.getContentHtml() : "<p>Draft agreement content</p>";
        AgreementTemplateVersion draft = buildVersionEntity(template, nextVersion, STATUS_DRAFT, baseContent, changeNotes);
        return toVersionDto(versionRepository.save(draft));
    }

    public AgreementTemplateVersionDTO updateDraftVersion(String versionId, AgreementVersionUpdateDTO dto) {
        AgreementTemplateVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Template version not found"));
        if (!STATUS_DRAFT.equals(version.getStatus())) {
            throw new RuntimeException("Only draft versions can be edited");
        }
        if (dto.getContentHtml() != null) {
            version.setContentHtml(HtmlSanitizerUtil.sanitizeAgreementHtml(dto.getContentHtml()));
        }
        if (dto.getChangeNotes() != null) {
            version.setChangeNotes(dto.getChangeNotes());
        }
        return toVersionDto(versionRepository.save(version));
    }

    @Transactional
    public AgreementTemplateVersionDTO publishVersion(String versionId, String publishedBy) {
        AgreementTemplateVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Template version not found"));
        if (!STATUS_DRAFT.equals(version.getStatus())) {
            throw new RuntimeException("Only draft versions can be published");
        }

        versionRepository.findByTemplate_IdAndStatus(version.getTemplate().getId(), STATUS_PUBLISHED)
                .ifPresent(existing -> {
                    existing.setStatus(STATUS_ARCHIVED);
                    versionRepository.save(existing);
                });

        version.setStatus(STATUS_PUBLISHED);
        version.setPublishedAt(LocalDateTime.now());
        version.setPublishedBy(publishedBy != null ? publishedBy : "admin");
        AgreementTemplateVersion saved = versionRepository.save(version);

        AgreementTemplate template = version.getTemplate();
        template.setUpdatedAt(LocalDateTime.now());
        templateRepository.save(template);

        return toVersionDto(saved);
    }

    public AgreementTemplateVersionDTO archiveVersion(String versionId) {
        AgreementTemplateVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Template version not found"));
        if (STATUS_PUBLISHED.equals(version.getStatus())) {
            throw new RuntimeException("Published versions cannot be archived directly");
        }
        version.setStatus(STATUS_ARCHIVED);
        return toVersionDto(versionRepository.save(version));
    }

    public AgreementPreviewResponseDTO previewVersion(String versionId, AgreementPreviewRequestDTO request) {
        AgreementTemplateVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Template version not found"));
        AgreementTemplate template = version.getTemplate();
        AgreementPreviewResponseDTO response = new AgreementPreviewResponseDTO();
        response.setTemplateKey(template.getTemplateKey());
        response.setVersionNumber(version.getVersionNumber());
        response.setMergedHtml(mergeService.mergeHtml(version.getContentHtml(), template, version, request));
        return response;
    }

    public AgreementAssetUploadResponseDTO uploadAsset(MultipartFile file, String templateId) throws Exception {
        String assetId = Micro_utils.idGenerator("agreementTemplateAsset", assetRepository.getLastId());
        String filePath = AgreementAssetFileUtil.storeAsset(file, templateId);

        AgreementTemplateAsset asset = new AgreementTemplateAsset();
        asset.setId(assetId);
        asset.setTemplateId(templateId);
        asset.setFilePath(filePath);
        asset.setOriginalName(file.getOriginalFilename());
        asset.setMimeType(file.getContentType());
        asset.setCreatedAt(LocalDateTime.now());
        assetRepository.save(asset);

        AgreementAssetUploadResponseDTO response = new AgreementAssetUploadResponseDTO();
        response.setAssetId(assetId);
        response.setFilePath(filePath);
        response.setUrl(mergeService.buildAssetUrl(assetId));
        return response;
    }

    public AgreementTemplateAsset getAsset(String assetId) {
        return assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
    }

    private AgreementTemplate ensureTemplate(String templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Agreement template not found"));
    }

    private AgreementTemplateVersion createDraftVersion(
            AgreementTemplate template,
            String contentHtml,
            String changeNotes
    ) {
        AgreementTemplateVersion version = buildVersionEntity(
                template,
                1,
                STATUS_DRAFT,
                contentHtml,
                changeNotes
        );
        return versionRepository.save(version);
    }

    private AgreementTemplateVersion buildVersionEntity(
            AgreementTemplate template,
            int versionNumber,
            String status,
            String contentHtml,
            String changeNotes
    ) {
        String versionId = Micro_utils.idGenerator("agreementTemplateVersion", versionRepository.getLastId());
        AgreementTemplateVersion version = new AgreementTemplateVersion();
        version.setId(versionId);
        version.setTemplate(template);
        version.setVersionNumber(versionNumber);
        version.setStatus(status);
        version.setContentHtml(HtmlSanitizerUtil.sanitizeAgreementHtml(contentHtml));
        version.setChangeNotes(changeNotes);
        version.setCreatedAt(LocalDateTime.now());
        return version;
    }

    private AgreementTemplateDTO toTemplateDto(AgreementTemplate template) {
        AgreementTemplateDTO dto = new AgreementTemplateDTO();
        dto.setId(template.getId());
        dto.setTemplateKey(template.getTemplateKey());
        dto.setDisplayName(template.getDisplayName());
        dto.setAudience(template.getAudience());
        dto.setCareGiverType(template.getCareGiverType());
        dto.setDefaultForAudience(template.isDefaultForAudience());
        dto.setStatus(template.getStatus());
        dto.setCreatedAt(template.getCreatedAt());
        dto.setUpdatedAt(template.getUpdatedAt());

        List<AgreementTemplateVersion> versions =
                versionRepository.findByTemplate_IdOrderByVersionNumberDesc(template.getId());
        dto.setVersions(versions.stream().map(this::toVersionSummary).collect(Collectors.toList()));

        versions.stream()
                .filter(v -> STATUS_PUBLISHED.equals(v.getStatus()))
                .findFirst()
                .ifPresent(published -> {
                    dto.setPublishedVersionNumber(published.getVersionNumber());
                    dto.setPublishedVersionId(published.getId());
                });
        return dto;
    }

    private AgreementTemplateVersionSummaryDTO toVersionSummary(AgreementTemplateVersion version) {
        AgreementTemplateVersionSummaryDTO dto = new AgreementTemplateVersionSummaryDTO();
        dto.setId(version.getId());
        dto.setVersionNumber(version.getVersionNumber());
        dto.setStatus(version.getStatus());
        dto.setChangeNotes(version.getChangeNotes());
        dto.setPublishedAt(version.getPublishedAt());
        dto.setPublishedBy(version.getPublishedBy());
        dto.setCreatedAt(version.getCreatedAt());
        return dto;
    }

    private AgreementTemplateVersionDTO toVersionDto(AgreementTemplateVersion version) {
        AgreementTemplateVersionDTO dto = new AgreementTemplateVersionDTO();
        dto.setId(version.getId());
        dto.setTemplateId(version.getTemplate().getId());
        dto.setTemplateKey(version.getTemplate().getTemplateKey());
        dto.setVersionNumber(version.getVersionNumber());
        dto.setStatus(version.getStatus());
        dto.setContentHtml(version.getContentHtml());
        dto.setChangeNotes(version.getChangeNotes());
        dto.setPublishedAt(version.getPublishedAt());
        dto.setPublishedBy(version.getPublishedBy());
        dto.setCreatedAt(version.getCreatedAt());
        return dto;
    }
}
