package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.AgreementPublicDocumentDTO;
import com.skycareSolutions.skyCarePortal.dto.AgreementPublicPreviewDTO;
import com.skycareSolutions.skyCarePortal.dto.AgreementRecipientSignDTO;
import com.skycareSolutions.skyCarePortal.dto.AgreementReferenceMaterialSummaryDTO;
import com.skycareSolutions.skyCarePortal.model.AgreementInstance;
import com.skycareSolutions.skyCarePortal.model.AgreementSignature;
import com.skycareSolutions.skyCarePortal.repository.AgreementInstanceRepository;
import com.skycareSolutions.skyCarePortal.repository.AgreementSignatureRepository;
import com.skycareSolutions.skyCarePortal.utils.AgreementHtmlImageInliner;
import com.skycareSolutions.skyCarePortal.utils.AgreementRateLimiter;
import com.skycareSolutions.skyCarePortal.utils.AgreementSigningTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AgreementPublicService {

    private static final String CONSENT_TEXT =
            "I have read and understood this agreement and agree to sign it electronically.";

    @Autowired
    private AgreementInstanceRepository instanceRepository;

    @Autowired
    private AgreementSignatureRepository agreementSignatureRepository;

    @Autowired
    private AgreementInstanceService instanceService;

    @Autowired
    private AgreementAuditService auditService;

    @Autowired
    private AgreementEmailService emailService;

    @Autowired
    private AgreementPdfService pdfService;

    @Autowired
    private OrganizationSettingService organizationSettingService;

    @Autowired
    private AgreementRateLimiter rateLimiter;

    @Autowired
    private AgreementReferenceMaterialService referenceMaterialService;

    @Value("${agreement.public.require-email-verification:false}")
    private boolean requireEmailVerification;

    public AgreementPublicDocumentDTO getDocument(String plainToken, String clientIp, String verifyEmail) {
        rateLimiter.checkOrThrow(clientIp != null ? clientIp : plainToken);
        AgreementInstance instance = resolveByToken(plainToken);

        if (AgreementInstanceService.STATUS_CANCELLED.equals(instance.getStatus())) {
            return buildStatusResponse(instance, false, false, true);
        }
        if (isExpired(instance)) {
            instance.setStatus(AgreementInstanceService.STATUS_EXPIRED);
            instance.setUpdatedAt(LocalDateTime.now());
            instanceRepository.save(instance);
            return buildStatusResponse(instance, true, false, false);
        }
        if (AgreementInstanceService.STATUS_SIGNED.equals(instance.getStatus())) {
            return buildSignedResponse(instance);
        }

        if (requireEmailVerification) {
            if (verifyEmail == null || verifyEmail.isBlank()
                    || !verifyEmail.equalsIgnoreCase(instance.getRecipientEmail())) {
                AgreementPublicDocumentDTO dto = new AgreementPublicDocumentDTO();
                dto.setRecipientName(maskName(instance.getRecipientName()));
                dto.setEmailVerificationRequired(true);
                dto.setOrganizationName(organizationSettingService.getEntity().getName());
                return dto;
            }
        }

        markViewed(instance);
        AgreementPublicDocumentDTO dto = buildDocumentResponse(instance);
        dto.setMergedHtml(instanceService.preparePublicHtml(instanceService.buildMergedHtml(instance), instance));
        return dto;
    }

    private AgreementPublicDocumentDTO buildStatusResponse(
            AgreementInstance instance, boolean expired, boolean signed, boolean cancelled) {
        AgreementPublicDocumentDTO dto = new AgreementPublicDocumentDTO();
        dto.setTemplateName(instance.getTemplateVersion().getTemplate().getDisplayName());
        dto.setRecipientName(instance.getRecipientName());
        dto.setStatus(instance.getStatus());
        dto.setExpired(expired);
        dto.setCancelled(cancelled);
        dto.setAlreadySigned(signed);
        return dto;
    }

    private AgreementPublicDocumentDTO buildSignedResponse(AgreementInstance instance) {
        AgreementPublicDocumentDTO dto = buildDocumentResponse(instance);
        dto.setAlreadySigned(true);
        dto.setMergedHtml(instanceService.preparePublicHtml(
                instance.getFinalHtmlSnapshot() != null
                        ? instance.getFinalHtmlSnapshot()
                        : instanceService.buildMergedHtml(instance),
                instance));
        return dto;
    }

    private AgreementPublicDocumentDTO buildDocumentResponse(AgreementInstance instance) {
        AgreementPublicDocumentDTO dto = new AgreementPublicDocumentDTO();
        dto.setTemplateName(instance.getTemplateVersion().getTemplate().getDisplayName());
        dto.setVersionNumber(instance.getTemplateVersion().getVersionNumber());
        dto.setRecipientName(instance.getRecipientName());
        dto.setOrganizationName(organizationSettingService.getEntity().getName());
        dto.setStatus(instance.getStatus());
        dto.setExpiresAt(instance.getExpiresAt());
        dto.setConsentText(CONSENT_TEXT);
        dto.setReferenceMaterials(referenceMaterialService.listForInstance(instance.getId()));
        return dto;
    }

    public byte[] downloadReferenceMaterial(String plainToken, String attachmentId, String clientIp) {
        rateLimiter.checkOrThrow(clientIp != null ? clientIp : plainToken);
        AgreementInstance instance = resolveByToken(plainToken);
        if (AgreementInstanceService.STATUS_CANCELLED.equals(instance.getStatus())) {
            throw new RuntimeException("Agreement is cancelled");
        }
        referenceMaterialService.getInstanceAttachment(instance.getId(), attachmentId);
        File file = referenceMaterialService.resolveInstanceAttachment(instance.getId(), attachmentId);
        try {
            return java.nio.file.Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            throw new RuntimeException("Unable to read reference material", e);
        }
    }

    public AgreementReferenceMaterialSummaryDTO getReferenceMaterialMeta(
            String plainToken,
            String attachmentId,
            String clientIp
    ) {
        rateLimiter.checkOrThrow(clientIp != null ? clientIp : plainToken);
        AgreementInstance instance = resolveByToken(plainToken);
        var attachment = referenceMaterialService.getInstanceAttachment(instance.getId(), attachmentId);
        AgreementReferenceMaterialSummaryDTO dto = new AgreementReferenceMaterialSummaryDTO();
        dto.setId(attachment.getId());
        dto.setTitle(attachment.getTitle());
        dto.setDescription(attachment.getDescription());
        dto.setOriginalFilename(attachment.getOriginalFilename());
        dto.setMimeType(attachment.getMimeType());
        return dto;
    }

    private String maskName(String name) {
        if (name == null || name.isBlank()) {
            return "Recipient";
        }
        return name;
    }

    private boolean isExpired(AgreementInstance instance) {
        return instance.getExpiresAt() != null && instance.getExpiresAt().isBefore(LocalDateTime.now());
    }

    public AgreementPublicDocumentDTO getDocument(String plainToken) {
        return getDocument(plainToken, plainToken, null);
    }

    @Transactional
    public AgreementPublicDocumentDTO sign(String plainToken, AgreementRecipientSignDTO dto, String clientIp) {
        rateLimiter.checkOrThrow(clientIp != null ? clientIp : plainToken);
        if (!dto.isConsentAccepted()) {
            throw new RuntimeException("Consent is required to sign");
        }
        AgreementInstance instance = resolveActiveInstance(plainToken);
        if (AgreementInstanceService.STATUS_SIGNED.equals(instance.getStatus())) {
            throw new RuntimeException("Agreement is already signed");
        }
        if (instance.getAdminSignatureId() == null) {
            throw new RuntimeException("Agreement is not ready for recipient signature");
        }

        String filePath = saveRecipientSignature(dto.getSignatureBase64(), instance.getRecipientId());
        AgreementSignature signature = new AgreementSignature();
        signature.setId(com.skycareSolutions.skyCarePortal.utils.Micro_utils.idGenerator(
                "agreementSignature", agreementSignatureRepository.getLastId()));
        signature.setOwnerType("RECIPIENT");
        signature.setOwnerId(instance.getRecipientId());
        signature.setSignatureType(dto.getSignatureType() != null ? dto.getSignatureType() : "DRAWN");
        signature.setFilePath(filePath);
        signature.setSavedForReuse(false);
        signature.setCreatedAt(LocalDateTime.now());
        agreementSignatureRepository.save(signature);

        instance.setRecipientSignatureId(signature.getId());
        instance.setStatus(AgreementInstanceService.STATUS_SIGNED);
        instance.setSignedAt(LocalDateTime.now());
        instance.setUpdatedAt(LocalDateTime.now());
        String finalHtml = instanceService.finalizeSignedHtml(instance, dto.getMarkedHtml());
        instance.setFinalHtmlSnapshot(finalHtml);
        instanceRepository.save(instance);

        generateSignedPdf(instance, finalHtml);

        auditService.log(instance.getId(), "RECIPIENT_SIGNED", instance.getRecipientId(), "RECIPIENT",
                Map.of("signatureId", signature.getId()));
        emailService.sendSignedConfirmation(instance);
        emailService.sendAdminNotification(instance);

        AgreementPublicDocumentDTO response = new AgreementPublicDocumentDTO();
        response.setTemplateName(instance.getTemplateVersion().getTemplate().getDisplayName());
        response.setVersionNumber(instance.getTemplateVersion().getVersionNumber());
        response.setRecipientName(instance.getRecipientName());
        response.setOrganizationName(organizationSettingService.getEntity().getName());
        response.setStatus(instance.getStatus());
        response.setAlreadySigned(true);
        response.setConsentText(CONSENT_TEXT);
        response.setMergedHtml(instanceService.preparePublicHtml(finalHtml, instance));
        return response;
    }

    @Transactional
    public AgreementPublicDocumentDTO sign(String plainToken, AgreementRecipientSignDTO dto) {
        return sign(plainToken, dto, plainToken);
    }

    public byte[] downloadPdf(String plainToken) {
        return downloadPdf(plainToken, plainToken);
    }

    public byte[] downloadPdf(String plainToken, String clientIp) {
        rateLimiter.checkOrThrow(clientIp != null ? clientIp : plainToken);
        AgreementInstance instance = resolveByToken(plainToken);
        if (AgreementInstanceService.STATUS_SIGNED.equals(instance.getStatus())
                && instance.getSignedPdfPath() != null) {
            File file = new File(instance.getSignedPdfPath());
            if (file.exists()) {
                try {
                    return java.nio.file.Files.readAllBytes(file.toPath());
                } catch (Exception e) {
                    throw new RuntimeException("Unable to read signed PDF", e);
                }
            }
        }
        String html = instanceService.preparePublicHtml(instanceService.buildMergedHtml(instance), instance);
        return pdfService.renderAgreementPdf(html, instance.getTemplateKey());
    }

    public byte[] downloadPreviewPdf(String plainToken, AgreementPublicPreviewDTO dto, String clientIp) {
        rateLimiter.checkOrThrow(clientIp != null ? clientIp : plainToken);
        AgreementInstance instance = resolveByToken(plainToken);
        if (AgreementInstanceService.STATUS_SIGNED.equals(instance.getStatus())) {
            return downloadPdf(plainToken, clientIp);
        }
        if (AgreementInstanceService.STATUS_CANCELLED.equals(instance.getStatus())) {
            throw new RuntimeException("This signing link has been cancelled");
        }
        if (isExpired(instance)) {
            throw new RuntimeException("This signing link has expired");
        }
        if (dto == null || dto.getSignatureBase64() == null || dto.getSignatureBase64().isBlank()) {
            throw new RuntimeException("Signature is required for preview PDF");
        }
        String html = instanceService.preparePublicHtml(
                instanceService.buildMergedHtmlWithRecipientPreview(instance, dto.getSignatureBase64()),
                instance);
        return pdfService.renderAgreementPdf(html, instance.getTemplateKey());
    }

    public AgreementPublicDocumentDTO previewHtml(String plainToken, AgreementPublicPreviewDTO dto, String clientIp) {
        rateLimiter.checkOrThrow(clientIp != null ? clientIp : plainToken);
        AgreementInstance instance = resolveByToken(plainToken);
        if (AgreementInstanceService.STATUS_SIGNED.equals(instance.getStatus())) {
            AgreementPublicDocumentDTO response = buildSignedResponse(instance);
            return response;
        }
        if (AgreementInstanceService.STATUS_CANCELLED.equals(instance.getStatus())) {
            throw new RuntimeException("This signing link has been cancelled");
        }
        if (isExpired(instance)) {
            throw new RuntimeException("This signing link has expired");
        }
        if (dto == null || dto.getSignatureBase64() == null || dto.getSignatureBase64().isBlank()) {
            throw new RuntimeException("Signature is required for preview");
        }
        AgreementPublicDocumentDTO response = new AgreementPublicDocumentDTO();
        response.setMergedHtml(instanceService.preparePublicHtml(
                instanceService.buildMergedHtmlWithRecipientPreview(instance, dto.getSignatureBase64()),
                instance));
        return response;
    }

    private void markViewed(AgreementInstance instance) {
        if (AgreementInstanceService.STATUS_SENT.equals(instance.getStatus())) {
            instance.setStatus(AgreementInstanceService.STATUS_VIEWED);
            instance.setViewedAt(LocalDateTime.now());
            instance.setUpdatedAt(LocalDateTime.now());
            instanceRepository.save(instance);
            auditService.log(instance.getId(), "INSTANCE_VIEWED", instance.getRecipientId(), "RECIPIENT", Map.of());
        }
    }

    private void generateSignedPdf(AgreementInstance instance, String html) {
        try {
            String prepared = instanceService.preparePublicHtml(html, instance);
            byte[] pdf = pdfService.renderAgreementPdf(prepared, instance.getTemplateKey());
            File dir = AgreementHtmlImageInliner.ensureInstanceDir(instance.getId());
            File out = new File(dir, "signed.pdf");
            try (FileOutputStream fos = new FileOutputStream(out)) {
                fos.write(pdf);
            }
            instance.setSignedPdfPath(out.getAbsolutePath());
            instanceRepository.save(instance);
            auditService.log(instance.getId(), "FINAL_PDF_GENERATED", instance.getRecipientId(), "SYSTEM", Map.of());
        } catch (Exception e) {
            throw new RuntimeException("Unable to generate signed PDF", e);
        }
    }

    private String saveRecipientSignature(String base64, String recipientId) {
        try {
            String data = base64.contains(",") ? base64.substring(base64.indexOf(',') + 1) : base64;
            byte[] bytes = java.util.Base64.getDecoder().decode(data);
            File dir = AgreementHtmlImageInliner.ensureSignaturesDir();
            File file = new File(dir, "recipient_" + recipientId + "_" + System.currentTimeMillis() + ".png");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(bytes);
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException("Unable to save recipient signature", e);
        }
    }

    private AgreementInstance resolveActiveInstance(String plainToken) {
        AgreementInstance instance = resolveByToken(plainToken);
        if (AgreementInstanceService.STATUS_CANCELLED.equals(instance.getStatus())) {
            throw new RuntimeException("This signing link has been cancelled");
        }
        if (instance.getExpiresAt() != null && instance.getExpiresAt().isBefore(LocalDateTime.now())) {
            instance.setStatus(AgreementInstanceService.STATUS_EXPIRED);
            instance.setUpdatedAt(LocalDateTime.now());
            instanceRepository.save(instance);
            throw new RuntimeException("This signing link has expired");
        }
        return instance;
    }

    private AgreementInstance resolveByToken(String plainToken) {
        String hash = AgreementSigningTokenUtil.hashToken(plainToken);
        return instanceRepository.findBySigningTokenHash(hash)
                .orElseThrow(() -> new RuntimeException("Invalid or expired signing link"));
    }
}
