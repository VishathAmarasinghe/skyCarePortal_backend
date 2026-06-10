package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.*;
import com.skycareSolutions.skyCarePortal.utils.AgreementHtmlImageInliner;
import com.skycareSolutions.skyCarePortal.utils.AgreementLogoUtil;
import com.skycareSolutions.skyCarePortal.utils.HtmlSanitizerUtil;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AgreementInstanceService {

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_ADMIN_SIGNED = "ADMIN_SIGNED";
    public static final String STATUS_SENT = "SENT";
    public static final String STATUS_VIEWED = "VIEWED";
    public static final String STATUS_SIGNED = "SIGNED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_EXPIRED = "EXPIRED";

    public static final String SEND_METHOD_SYSTEM_EMAIL = "SYSTEM_EMAIL";
    public static final String SEND_METHOD_MANUAL_LINK = "MANUAL_LINK";
    public static final String SEND_METHOD_MANUAL_PDF = "MANUAL_PDF";

    private static final int DEFAULT_EXPIRY_DAYS = 14;
    private static final String UNSPECIFIED_CARE_GIVER_TYPE = "Not specified";
    private static final String RETIRED_WORKER_TEMPLATE_KEY = AgreementSeedContent.RETIRED_WORKER_TEMPLATE_KEY;

    @Autowired
    private AgreementInstanceRepository instanceRepository;

    @Autowired
    private AgreementTemplateRepository templateRepository;

    @Autowired
    private AgreementTemplateVersionRepository versionRepository;

    @Autowired
    private AgreementTemplateAssignmentRepository assignmentRepository;

    @Autowired
    private AgreementSignatureRepository agreementSignatureRepository;

    @Autowired
    private AdminSavedSignatureRepository adminSavedSignatureRepository;

    @Autowired
    private SignatureRepository signatureRepository;

    @Autowired
    private CareGiverRepository careGiverRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AgreementMergeService mergeService;

    @Autowired
    private AgreementAuditService auditService;

    @Autowired
    private AgreementPdfService pdfService;

    @Autowired
    private AgreementEventRepository eventRepository;

    @Autowired
    private AgreementTemplateAssetRepository assetRepository;

    @Autowired
    private OrganizationSettingService organizationSettingService;

    @Autowired
    private AgreementEmailService agreementEmailService;

    @Autowired
    private AgreementReferenceMaterialService referenceMaterialService;

    @Autowired
    @Lazy
    private AgreementInstanceService self;

    @Value("${app.external.service.baseUrl:http://localhost:5000}")
    private String backendBaseUrl;

    @Value("${app.external.service.frontendUrl:http://localhost:5173}")
    private String frontendUrl;

    public List<AgreementApplicableTemplateDTO> getApplicableTemplates(String recipientType, String recipientId) {
        if ("WORKER".equalsIgnoreCase(recipientType)) {
            return getApplicableWorkerTemplates(recipientId);
        }
        if ("CLIENT".equalsIgnoreCase(recipientType)) {
            return getApplicableClientTemplates();
        }
        return List.of();
    }

    public List<AgreementInstanceDTO> getByRecipient(String recipientType, String recipientId) {
        return instanceRepository.findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(recipientType, recipientId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AgreementInstanceDTO createInstance(AgreementInstanceCreateDTO dto) {
        AgreementTemplateVersion version = resolveVersion(dto);
        AgreementTemplate template = version.getTemplate();
        RecipientInfo recipient = resolveRecipient(dto.getRecipientType(), dto.getRecipientId());

        String adminSigHtml = resolveAdminSignatureHtml(dto.getCreatedBy(), null);
        String contentSource = version.getContentHtml();
        String merged = mergeService.mergeForInstance(
                contentSource, template, version,
                dto.getRecipientType(), dto.getRecipientId(), adminSigHtml, "");

        AgreementInstance instance = new AgreementInstance();
        instance.setId(Micro_utils.idGenerator("agreementInstance", instanceRepository.getLastId()));
        instance.setTemplateVersion(version);
        instance.setTemplateKey(template.getTemplateKey());
        instance.setRecipientType(dto.getRecipientType());
        instance.setRecipientId(dto.getRecipientId());
        instance.setRecipientEmail(recipient.email());
        instance.setRecipientName(recipient.name());
        instance.setStatus(STATUS_DRAFT);
        instance.setBaseMergedHtml(merged);
        instance.setCreatedBy(dto.getCreatedBy());
        instance.setCreatedAt(LocalDateTime.now());
        instance.setUpdatedAt(LocalDateTime.now());
        instance = instanceRepository.save(instance);

        auditService.log(instance.getId(), "INSTANCE_CREATED", dto.getCreatedBy(), "ADMIN",
                Map.of("templateKey", template.getTemplateKey(), "version", version.getVersionNumber()));
        return toDto(instance);
    }

    public AgreementPreviewResponseDTO previewInstance(String instanceId) {
        AgreementInstance instance = getInstance(instanceId);
        String html = prepareHtmlForOutput(buildMergedHtml(instance), instance);
        AgreementPreviewResponseDTO response = new AgreementPreviewResponseDTO();
        response.setMergedHtml(html);
        response.setTemplateKey(instance.getTemplateKey());
        response.setVersionNumber(instance.getTemplateVersion().getVersionNumber());
        auditService.log(instanceId, "PREVIEW_GENERATED", instance.getCreatedBy(), "ADMIN", Map.of());
        return response;
    }

    public byte[] downloadPdf(String instanceId) {
        AgreementInstance instance = getInstance(instanceId);
        if (STATUS_SIGNED.equals(instance.getStatus()) && instance.getSignedPdfPath() != null) {
            File signed = new File(instance.getSignedPdfPath());
            if (signed.exists()) {
                try {
                    byte[] pdf = java.nio.file.Files.readAllBytes(signed.toPath());
                    auditService.log(instanceId, "PDF_DOWNLOADED", instance.getCreatedBy(), "ADMIN", Map.of("type", "signed"));
                    return pdf;
                } catch (Exception ignored) {
                }
            }
        }
        String html = prepareHtmlForOutput(buildMergedHtml(instance), instance);
        byte[] pdf = pdfService.renderAgreementPdf(html, instance.getTemplateKey());
        try {
            File dir = AgreementHtmlImageInliner.ensureInstanceDir(instanceId);
            String fileName = instance.getAdminSignatureId() != null ? "admin_signed.pdf" : "preview.pdf";
            File out = new File(dir, fileName);
            try (FileOutputStream fos = new FileOutputStream(out)) {
                fos.write(pdf);
            }
            if (instance.getAdminSignatureId() != null) {
                instance.setAdminSignedPdfPath(out.getAbsolutePath());
            } else {
                instance.setUnsignedPdfPath(out.getAbsolutePath());
            }
            instance.setUpdatedAt(LocalDateTime.now());
            instanceRepository.save(instance);
            auditService.log(instanceId, "PDF_DOWNLOADED", instance.getCreatedBy(), "ADMIN", Map.of());
        } catch (Exception ignored) {
        }
        return pdf;
    }

    @Transactional
    public AgreementInstanceDTO updateContent(String instanceId, AgreementInstanceContentDTO dto, String actorId) {
        AgreementInstance instance = getInstance(instanceId);
        instance.setCustomContentHtml(HtmlSanitizerUtil.sanitizeAgreementHtml(dto.getCustomContentHtml()));
        instance.setUpdatedAt(LocalDateTime.now());
        instanceRepository.save(instance);
        auditService.log(instanceId, "INSTANCE_CONTENT_OVERRIDDEN", actorId, "ADMIN", Map.of());
        return toDto(instance);
    }

    @Transactional
    public AgreementInstanceDTO adminSign(String instanceId, AgreementAdminSignDTO dto) {
        AgreementInstance instance = getInstance(instanceId);
        String filePath = resolveSignatureFile(dto);
        AgreementSignature signature = new AgreementSignature();
        signature.setId(Micro_utils.idGenerator("agreementSignature", agreementSignatureRepository.getLastId()));
        signature.setOwnerType("ADMIN");
        signature.setOwnerId(dto.getAdminEmployeeId());
        signature.setSignatureType(dto.getSignatureType() != null ? dto.getSignatureType() : "DRAWN");
        signature.setFilePath(filePath);
        signature.setSavedForReuse(dto.isSaveForReuse());
        signature.setCreatedAt(LocalDateTime.now());
        agreementSignatureRepository.save(signature);

        if (dto.isSaveForReuse()) {
            saveAdminDefaultSignature(dto.getAdminEmployeeId(), signature.getId());
        }

        instance.setAdminSignatureId(signature.getId());
        instance.setStatus(STATUS_ADMIN_SIGNED);
        instance.setFinalHtmlSnapshot(buildMergedHtml(instance));
        instance.setUpdatedAt(LocalDateTime.now());
        instanceRepository.save(instance);

        auditService.log(instanceId, "ADMIN_SIGNED", dto.getAdminEmployeeId(), "ADMIN",
                Map.of("signatureId", signature.getId(), "reuse", dto.isReuseSavedSignature()));
        return toDto(instance);
    }

    public AgreementSendResponseDTO sendInstance(String instanceId, AgreementSendDTO dto) {
        PreparedAgreementSend prepared = self.persistAgreementSendState(instanceId, dto);

        try {
            agreementEmailService.sendInviteEmail(prepared.instance(), prepared.plainToken(), dto);
        } catch (Exception e) {
            throw new RuntimeException("Agreement prepared but email failed: " + e.getMessage(), e);
        }

        auditService.log(instanceId, "INSTANCE_SENT", dto.getSentBy(), "ADMIN",
                Map.of(
                        "recipientEmail", prepared.instance().getRecipientEmail() != null
                                ? prepared.instance().getRecipientEmail() : "",
                        "sendMethod", SEND_METHOD_SYSTEM_EMAIL,
                        "expiresAt", prepared.instance().getExpiresAt() != null
                                ? prepared.instance().getExpiresAt().toString() : "",
                        "referenceMaterialCount", dto.getReferenceMaterialIds() != null
                                ? dto.getReferenceMaterialIds().size() : 0));

        AgreementInstance detailed = instanceRepository.findDetailedById(instanceId)
                .orElseThrow(() -> new RuntimeException("Agreement instance not found"));
        return buildSendResponse(detailed, "Agreement sent via system email", prepared.plainToken());
    }

    @Transactional
    public PreparedAgreementSend persistAgreementSendState(String instanceId, AgreementSendDTO dto) {
        AgreementInstance instance = getInstance(instanceId);
        assertReadyForFirstSend(instance);

        String plainToken = issueSigningToken(instance, dto.getExpiresAt());
        if (dto.getRecipientEmail() != null && !dto.getRecipientEmail().isBlank()) {
            instance.setRecipientEmail(dto.getRecipientEmail());
        }
        instance.setEmailTemplateId(dto.getEmailTemplateId());
        instance.setEmailSubjectSnapshot(dto.getSubjectOverride());
        instance.setEmailBodySnapshot(dto.getBodyHtmlOverride());
        instance.setEmailCcSnapshot(formatCcSnapshot(dto.getCcEmails()));
        instance.setSentBy(dto.getSentBy());
        instance.setSentAt(LocalDateTime.now());
        instance.setSendMethod(SEND_METHOD_SYSTEM_EMAIL);
        instance.setManualSendNotes(null);
        instance.setStatus(STATUS_SENT);
        instance.setUpdatedAt(LocalDateTime.now());
        instance.setFinalHtmlSnapshot(buildMergedHtml(instance));
        instanceRepository.save(instance);

        referenceMaterialService.attachToInstance(instanceId, dto.getReferenceMaterialIds());
        return new PreparedAgreementSend(instance, plainToken);
    }

    @Transactional
    public AgreementSendResponseDTO markSentManually(String instanceId, AgreementManualSendDTO dto) {
        AgreementInstance instance = getInstance(instanceId);
        assertReadyForFirstSend(instance);

        String method = dto.getSendMethod() != null ? dto.getSendMethod() : SEND_METHOD_MANUAL_PDF;
        if (!SEND_METHOD_MANUAL_LINK.equals(method) && !SEND_METHOD_MANUAL_PDF.equals(method)) {
            throw new RuntimeException("Invalid manual send method: " + method);
        }

        String plainToken = null;
        if (SEND_METHOD_MANUAL_LINK.equals(method)) {
            plainToken = issueSigningToken(instance, dto.getExpiresAt());
        } else {
            instance.setSigningTokenHash(null);
            if (dto.getExpiresAt() != null) {
                instance.setExpiresAt(dto.getExpiresAt());
            }
        }

        instance.setSendMethod(method);
        instance.setManualSendNotes(dto.getNotes());
        instance.setSentBy(dto.getActorId());
        instance.setSentAt(LocalDateTime.now());
        instance.setStatus(STATUS_SENT);
        instance.setUpdatedAt(LocalDateTime.now());
        instance.setFinalHtmlSnapshot(buildMergedHtml(instance));
        instanceRepository.save(instance);

        referenceMaterialService.attachToInstance(instanceId, dto.getReferenceMaterialIds());

        auditService.log(instanceId, "INSTANCE_SENT_MANUAL", dto.getActorId(), "ADMIN",
                Map.of(
                        "sendMethod", method,
                        "notes", dto.getNotes() != null ? dto.getNotes() : "",
                        "referenceMaterialCount", dto.getReferenceMaterialIds() != null
                                ? dto.getReferenceMaterialIds().size() : 0));

        String message = SEND_METHOD_MANUAL_LINK.equals(method)
                ? "Agreement marked as sent manually (signing link ready to share)"
                : "Agreement marked as sent manually (PDF delivered offline)";
        AgreementInstance detailed = instanceRepository.findDetailedById(instanceId)
                .orElseThrow(() -> new RuntimeException("Agreement instance not found"));
        return buildSendResponse(detailed, message, plainToken);
    }

    @Transactional
    public AgreementSigningLinkDTO generateSigningLink(String instanceId, String actorId, boolean regenerate) {
        AgreementInstance instance = getInstance(instanceId);
        if (instance.getAdminSignatureId() == null) {
            throw new RuntimeException("Admin signature is required before generating a signing link");
        }
        if (STATUS_SIGNED.equals(instance.getStatus()) || STATUS_CANCELLED.equals(instance.getStatus())) {
            throw new RuntimeException("Cannot generate signing link in status: " + instance.getStatus());
        }
        if (!regenerate && instance.getSigningTokenHash() != null
                && instance.getExpiresAt() != null
                && instance.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("An active signing link already exists. Use regenerate to create a new link.");
        }

        String plainToken = issueSigningToken(instance, instance.getExpiresAt());
        if (instance.getSentAt() == null) {
            instance.setSentAt(LocalDateTime.now());
        }
        if (instance.getSendMethod() == null) {
            instance.setSendMethod(SEND_METHOD_MANUAL_LINK);
        }
        if (!STATUS_SENT.equals(instance.getStatus())
                && !STATUS_VIEWED.equals(instance.getStatus())
                && !STATUS_EXPIRED.equals(instance.getStatus())) {
            instance.setStatus(STATUS_SENT);
        }
        instance.setUpdatedAt(LocalDateTime.now());
        instanceRepository.save(instance);

        auditService.log(instanceId, "SIGNING_LINK_GENERATED", actorId, "ADMIN",
                Map.of("regenerate", regenerate));

        AgreementSigningLinkDTO response = new AgreementSigningLinkDTO();
        response.setSigningUrl(buildSigningUrl(plainToken));
        response.setExpiresAt(instance.getExpiresAt());
        response.setInstance(toDto(instance));
        response.setMessage("Signing link generated");
        return response;
    }

    @Transactional
    public AgreementInstanceDTO uploadSignedPdf(String instanceId, byte[] pdfBytes, String actorId, String notes) {
        AgreementInstance instance = getInstance(instanceId);
        if (instance.getAdminSignatureId() == null) {
            throw new RuntimeException("Admin signature is required before uploading a signed PDF");
        }
        if (STATUS_CANCELLED.equals(instance.getStatus())) {
            throw new RuntimeException("Cannot upload signed PDF for cancelled agreement");
        }
        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new RuntimeException("PDF file is required");
        }

        try {
            File dir = AgreementHtmlImageInliner.ensureInstanceDir(instanceId);
            File out = new File(dir, "uploaded_signed_" + System.currentTimeMillis() + ".pdf");
            try (FileOutputStream fos = new FileOutputStream(out)) {
                fos.write(pdfBytes);
            }
            deletePdfFile(instance.getSignedPdfPath());
            instance.setSignedPdfPath(out.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Unable to store signed PDF", e);
        }

        instance.setStatus(STATUS_SIGNED);
        instance.setSignedViaUpload(true);
        instance.setSignedAt(LocalDateTime.now());
        instance.setSigningTokenHash(null);
        instance.setUpdatedAt(LocalDateTime.now());
        if (notes != null && !notes.isBlank()) {
            instance.setManualSendNotes(
                    (instance.getManualSendNotes() != null ? instance.getManualSendNotes() + " | " : "") + notes);
        }
        instanceRepository.save(instance);

        auditService.log(instanceId, "SIGNED_PDF_UPLOADED", actorId, "ADMIN",
                Map.of("notes", notes != null ? notes : ""));

        return toDto(instance);
    }

    @Transactional
    public AgreementInstanceDTO clearAdminSignature(String instanceId, String actorId) {
        AgreementInstance instance = getInstance(instanceId);
        if (STATUS_SIGNED.equals(instance.getStatus())) {
            throw new RuntimeException("Cannot clear admin signature on a signed agreement");
        }
        if (STATUS_CANCELLED.equals(instance.getStatus())) {
            throw new RuntimeException("Cannot clear admin signature on a cancelled agreement");
        }

        instance.setAdminSignatureId(null);
        instance.setAdminSignedPdfPath(null);
        instance.setSigningTokenHash(null);
        instance.setStatus(STATUS_DRAFT);
        instance.setSendMethod(null);
        instance.setManualSendNotes(null);
        instance.setSentAt(null);
        instance.setUpdatedAt(LocalDateTime.now());
        instanceRepository.save(instance);

        auditService.log(instanceId, "ADMIN_SIGNATURE_CLEARED", actorId, "ADMIN", Map.of());
        return toDto(instance);
    }

    @Transactional
    public AgreementInstanceDTO updateStatus(String instanceId, AgreementStatusUpdateDTO dto) {
        AgreementInstance instance = getInstance(instanceId);
        String newStatus = dto.getStatus();
        if (newStatus == null || newStatus.isBlank()) {
            throw new RuntimeException("Status is required");
        }
        Set<String> allowed = Set.of(
                STATUS_DRAFT, STATUS_ADMIN_SIGNED, STATUS_SENT, STATUS_VIEWED,
                STATUS_SIGNED, STATUS_CANCELLED, STATUS_EXPIRED);
        if (!allowed.contains(newStatus)) {
            throw new RuntimeException("Invalid status: " + newStatus);
        }

        String previous = instance.getStatus();
        instance.setStatus(newStatus);
        instance.setUpdatedAt(LocalDateTime.now());

        if (STATUS_CANCELLED.equals(newStatus)) {
            instance.setSigningTokenHash(null);
        }
        if (STATUS_SIGNED.equals(newStatus) && instance.getSignedAt() == null) {
            instance.setSignedAt(LocalDateTime.now());
        }
        if (STATUS_SENT.equals(newStatus) && instance.getSentAt() == null) {
            instance.setSentAt(LocalDateTime.now());
        }

        instanceRepository.save(instance);
        auditService.log(instanceId, "STATUS_UPDATED", dto.getActorId(), "ADMIN",
                Map.of("from", previous, "to", newStatus, "reason", dto.getReason() != null ? dto.getReason() : ""));

        return toDto(instance);
    }

    public List<AgreementReferenceMaterialSummaryDTO> getInstanceReferenceMaterials(String instanceId) {
        getInstance(instanceId);
        return referenceMaterialService.listForInstance(instanceId);
    }

    @Transactional
    public AgreementSendResponseDTO resendInstance(String instanceId, String actorId, boolean sendEmail) {
        AgreementInstance instance = getInstance(instanceId);
        if (!STATUS_SENT.equals(instance.getStatus())
                && !STATUS_VIEWED.equals(instance.getStatus())
                && !STATUS_EXPIRED.equals(instance.getStatus())) {
            throw new RuntimeException("Only sent, viewed, or expired agreements can be resent");
        }

        String plainToken = issueSigningToken(instance, instance.getExpiresAt());
        instance.setStatus(STATUS_SENT);
        instance.setUpdatedAt(LocalDateTime.now());
        instanceRepository.save(instance);

        if (sendEmail) {
            agreementEmailService.sendResendInvite(instance, plainToken);
        }

        auditService.log(instanceId, sendEmail ? "INSTANCE_RESENT" : "SIGNING_LINK_REGENERATED", actorId, "ADMIN",
                Map.of("sendEmail", sendEmail));

        return buildSendResponse(instance,
                sendEmail ? "Agreement resent via email" : "New signing link generated",
                plainToken);
    }

    @Transactional
    public AgreementInstanceDTO cancelInstance(String instanceId, String actorId) {
        AgreementInstance instance = getInstance(instanceId);
        if (STATUS_SIGNED.equals(instance.getStatus()) || STATUS_CANCELLED.equals(instance.getStatus())) {
            throw new RuntimeException("Agreement cannot be cancelled in status: " + instance.getStatus());
        }
        instance.setStatus(STATUS_CANCELLED);
        instance.setSigningTokenHash(null);
        instance.setUpdatedAt(LocalDateTime.now());
        instanceRepository.save(instance);
        auditService.log(instanceId, "INSTANCE_CANCELLED", actorId, "ADMIN", Map.of());
        return toDto(instance);
    }

    @Transactional
    public void deleteInstance(String instanceId, String actorId) {
        AgreementInstance instance = getInstance(instanceId);
        if (!STATUS_CANCELLED.equals(instance.getStatus())) {
            throw new RuntimeException("Only cancelled agreements can be deleted");
        }
        eventRepository.findByInstanceIdOrderByCreatedAtDesc(instanceId)
                .forEach(eventRepository::delete);
        deletePdfFile(instance.getUnsignedPdfPath());
        deletePdfFile(instance.getAdminSignedPdfPath());
        deletePdfFile(instance.getSignedPdfPath());
        instanceRepository.delete(instance);
    }

    public String preparePublicHtml(String html, AgreementInstance instance) {
        return prepareHtmlForOutput(html, instance);
    }

    public List<AgreementEventDTO> getEvents(String instanceId) {
        return eventRepository.findByInstanceIdOrderByCreatedAtDesc(instanceId).stream()
                .map(this::toEventDto)
                .collect(Collectors.toList());
    }

    public String buildMergedHtml(AgreementInstance instance) {
        AgreementTemplateVersion version = instance.getTemplateVersion();
        AgreementTemplate template = version.getTemplate();
        String content = instance.getCustomContentHtml() != null && !instance.getCustomContentHtml().isBlank()
                ? instance.getCustomContentHtml()
                : version.getContentHtml();
        String adminSigHtml = resolveAdminSignatureHtml(instance.getCreatedBy(), instance.getAdminSignatureId());
        String recipientSigHtml = resolveRecipientSignatureHtml(instance.getRecipientSignatureId());
        LocalDateTime adminSignedAt = resolveAdminSignedAt(instance);
        return mergeService.mergeForInstance(
                content, template, version,
                instance.getRecipientType(), instance.getRecipientId(),
                adminSigHtml, recipientSigHtml, adminSignedAt, instance.getSignedAt());
    }

    public String buildMergedHtmlWithRecipientPreview(AgreementInstance instance, String signatureBase64) {
        AgreementTemplateVersion version = instance.getTemplateVersion();
        AgreementTemplate template = version.getTemplate();
        String content = instance.getCustomContentHtml() != null && !instance.getCustomContentHtml().isBlank()
                ? instance.getCustomContentHtml()
                : version.getContentHtml();
        String adminSigHtml = resolveAdminSignatureHtml(instance.getCreatedBy(), instance.getAdminSignatureId());
        String recipientSigHtml = buildSignatureImgFromBase64(signatureBase64);
        LocalDateTime adminSignedAt = resolveAdminSignedAt(instance);
        return mergeService.mergeForInstance(
                content, template, version,
                instance.getRecipientType(), instance.getRecipientId(),
                adminSigHtml, recipientSigHtml, adminSignedAt, LocalDateTime.now());
    }

    public String finalizeSignedHtml(AgreementInstance instance, String markedHtml) {
        String official = buildMergedHtml(instance);
        if (markedHtml == null || markedHtml.isBlank()) {
            return official;
        }
        String sanitized = HtmlSanitizerUtil.sanitizeAgreementHtml(markedHtml);
        if (!sanitized.contains("agreement-signature-section")
                || !sanitized.contains("Recipient signature")) {
            return official;
        }
        return sanitized;
    }

    private String buildSignatureImgFromBase64(String base64) {
        if (base64 == null || base64.isBlank()) {
            return "";
        }
        String src = base64.contains(",") ? base64 : "data:image/png;base64," + base64;
        return "<img src=\"" + src.replace("\"", "") + "\" alt=\"Recipient signature\" "
                + "class=\"agreement-signature-image\" style=\"max-height:80px;max-width:240px;display:block;\" />";
    }

    private LocalDateTime resolveAdminSignedAt(AgreementInstance instance) {
        if (instance.getAdminSignatureId() == null) {
            return null;
        }
        return agreementSignatureRepository.findById(instance.getAdminSignatureId())
                .map(AgreementSignature::getCreatedAt)
                .orElse(null);
    }

    private String resolveRecipientSignatureHtml(String agreementSignatureId) {
        if (agreementSignatureId == null) {
            return "";
        }
        return agreementSignatureRepository.findById(agreementSignatureId)
                .map(sig -> buildSignatureImg(sig.getFilePath(), "Recipient signature"))
                .orElse("");
    }

    private String resolveAdminSignatureHtml(String adminEmployeeId, String agreementSignatureId) {
        if (agreementSignatureId != null) {
            return agreementSignatureRepository.findById(agreementSignatureId)
                    .map(sig -> buildSignatureImg(sig.getFilePath(), "Admin signature"))
                    .orElse("");
        }
        if (adminEmployeeId != null) {
            Optional<AdminSavedSignature> saved = adminSavedSignatureRepository.findByAdminEmployeeId(adminEmployeeId);
            if (saved.isPresent()) {
                return agreementSignatureRepository.findById(saved.get().getSignatureId())
                        .map(sig -> buildSignatureImg(sig.getFilePath(), "Admin signature"))
                        .orElse("");
            }
            return signatureRepository.findByEmployeeID(adminEmployeeId)
                    .map(sig -> buildSignatureImg(sig.getSignature(), "Admin signature"))
                    .orElse("");
        }
        return "";
    }

    private String buildSignatureImg(String filePath, String alt) {
        String dataUrl = AgreementHtmlImageInliner.fileToDataUrl(filePath);
        if (dataUrl.isBlank()) {
            return "";
        }
        return "<img src=\"" + dataUrl + "\" alt=\"" + alt + "\" class=\"agreement-signature-image\" style=\"max-height:80px;max-width:240px;display:block;\" />";
    }

    private String resolveSignatureFile(AgreementAdminSignDTO dto) {
        if (dto.isReuseSavedSignature()) {
            Optional<AdminSavedSignature> saved =
                    adminSavedSignatureRepository.findByAdminEmployeeId(dto.getAdminEmployeeId());
            if (saved.isPresent()) {
                AgreementSignature sig = agreementSignatureRepository.findById(saved.get().getSignatureId())
                        .orElseThrow(() -> new RuntimeException("Saved signature not found"));
                return sig.getFilePath();
            }
            Signature empSig = signatureRepository.findByEmployeeID(dto.getAdminEmployeeId())
                    .orElseThrow(() -> new RuntimeException("No saved signature for admin"));
            return empSig.getSignature();
        }
        if (dto.getSignatureBase64() == null || dto.getSignatureBase64().isBlank()) {
            throw new RuntimeException("Signature data is required");
        }
        return saveBase64Signature(dto.getSignatureBase64(), dto.getAdminEmployeeId());
    }

    private String saveBase64Signature(String base64, String employeeId) {
        try {
            String data = base64.contains(",") ? base64.substring(base64.indexOf(',') + 1) : base64;
            byte[] bytes = Base64.getDecoder().decode(data);
            File dir = AgreementHtmlImageInliner.ensureSignaturesDir();
            File file = new File(dir, employeeId + "_" + System.currentTimeMillis() + ".png");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(bytes);
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException("Unable to save signature", e);
        }
    }

    private void saveAdminDefaultSignature(String adminEmployeeId, String signatureId) {
        AdminSavedSignature saved = adminSavedSignatureRepository.findByAdminEmployeeId(adminEmployeeId)
                .orElse(new AdminSavedSignature());
        if (saved.getId() == null) {
            saved.setId(Micro_utils.idGenerator("adminSavedSignature", adminSavedSignatureRepository.getLastId()));
        }
        saved.setAdminEmployeeId(adminEmployeeId);
        saved.setSignatureId(signatureId);
        saved.setDefaultSignature(true);
        saved.setUpdatedAt(LocalDateTime.now());
        adminSavedSignatureRepository.save(saved);
    }

    private AgreementTemplateVersion resolveVersion(AgreementInstanceCreateDTO dto) {
        if (dto.getTemplateVersionId() != null && !dto.getTemplateVersionId().isBlank()) {
            return versionRepository.findById(dto.getTemplateVersionId())
                    .orElseThrow(() -> new RuntimeException("Template version not found"));
        }
        if (dto.getTemplateKey() != null && !dto.getTemplateKey().isBlank()) {
            AgreementTemplate template = templateRepository.findByTemplateKey(dto.getTemplateKey())
                    .orElseThrow(() -> new RuntimeException("Template not found"));
            if ("WORKER".equalsIgnoreCase(dto.getRecipientType())) {
                validateWorkerCanReceiveAgreement(dto.getRecipientId(), template);
            }
            return versionRepository.findByTemplate_IdAndStatus(template.getId(), AgreementTemplateService.STATUS_PUBLISHED)
                    .orElseThrow(() -> new RuntimeException("No published version for template"));
        }
        List<AgreementApplicableTemplateDTO> applicable =
                getApplicableTemplates(dto.getRecipientType(), dto.getRecipientId());
        if (applicable.isEmpty()) {
            throw new RuntimeException("No applicable agreement template found");
        }
        return versionRepository.findById(applicable.get(0).getPublishedVersionId())
                .orElseThrow(() -> new RuntimeException("Published version not found"));
    }

    private List<AgreementApplicableTemplateDTO> getApplicableWorkerTemplates(String careGiverId) {
        CareGiver careGiver = careGiverRepository.findById(careGiverId)
                .orElseThrow(() -> new RuntimeException("Care giver not found"));
        String careGiverType = careGiver.getCareGiverType() != null ? careGiver.getCareGiverType() : UNSPECIFIED_CARE_GIVER_TYPE;

        if (isUnspecifiedCareGiverType(careGiverType)) {
            return List.of();
        }

        Optional<AgreementTemplateAssignment> assignment = assignmentRepository.findAllByOrderByPriorityAsc()
                .stream()
                .filter(a -> careGiverType.equals(a.getCareGiverType()))
                .findFirst();

        if (assignment.isPresent()) {
            AgreementTemplate template = assignment.get().getTemplate();
            AgreementApplicableTemplateDTO applicable = toApplicable(template);
            return applicable != null ? List.of(applicable) : List.of();
        }

        return templateRepository.findByAudienceAndCareGiverType("WORKER", careGiverType).stream()
                .map(this::toApplicable)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void validateWorkerCanReceiveAgreement(String careGiverId, AgreementTemplate template) {
        CareGiver careGiver = careGiverRepository.findById(careGiverId)
                .orElseThrow(() -> new RuntimeException("Care giver not found"));
        if (isUnspecifiedCareGiverType(careGiver.getCareGiverType())) {
            throw new RuntimeException(
                    "Cannot send an agreement until the worker's care giver type is set (Not specified is not a valid agreement type)"
            );
        }
        if (isRetiredWorkerTemplate(template) || !"Active".equalsIgnoreCase(template.getStatus())) {
            throw new RuntimeException("This agreement template is not available");
        }
    }

    private static boolean isUnspecifiedCareGiverType(String careGiverType) {
        if (careGiverType == null || careGiverType.isBlank()) {
            return true;
        }
        return UNSPECIFIED_CARE_GIVER_TYPE.equalsIgnoreCase(careGiverType.trim());
    }

    private static boolean isRetiredWorkerTemplate(AgreementTemplate template) {
        return template != null && RETIRED_WORKER_TEMPLATE_KEY.equals(template.getTemplateKey());
    }

    private List<AgreementApplicableTemplateDTO> getApplicableClientTemplates() {
        return templateRepository.findByAudience("CLIENT").stream()
                .map(this::toApplicable)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private AgreementApplicableTemplateDTO toApplicable(AgreementTemplate template) {
        if (template == null
                || isRetiredWorkerTemplate(template)
                || !"Active".equalsIgnoreCase(template.getStatus())) {
            return null;
        }
        Optional<AgreementTemplateVersion> published =
                versionRepository.findByTemplate_IdAndStatus(template.getId(), AgreementTemplateService.STATUS_PUBLISHED);
        if (published.isEmpty()) {
            return null;
        }
        AgreementApplicableTemplateDTO dto = new AgreementApplicableTemplateDTO();
        dto.setTemplateId(template.getId());
        dto.setTemplateKey(template.getTemplateKey());
        dto.setDisplayName(template.getDisplayName());
        dto.setPublishedVersionId(published.get().getId());
        dto.setPublishedVersionNumber(published.get().getVersionNumber());
        dto.setCareGiverType(template.getCareGiverType());
        dto.setDefaultForAudience(template.isDefaultForAudience());
        return dto;
    }

    private RecipientInfo resolveRecipient(String recipientType, String recipientId) {
        if ("WORKER".equalsIgnoreCase(recipientType)) {
            CareGiver cg = careGiverRepository.findById(recipientId)
                    .orElseThrow(() -> new RuntimeException("Care giver not found"));
            Employee emp = cg.getEmployee();
            return new RecipientInfo(
                    emp.getFirstName() + " " + emp.getLastName(),
                    emp.getEmail() != null ? emp.getEmail() : ""
            );
        }
        Client client = clientRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return new RecipientInfo(
                client.getFirstName() + " " + client.getLastName(),
                client.getEmail() != null ? client.getEmail() : ""
        );
    }

    private AgreementInstance getInstance(String id) {
        return instanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agreement instance not found"));
    }

    public AgreementInstance getInstanceEntity(String id) {
        return getInstance(id);
    }

    private AgreementInstanceDTO toDto(AgreementInstance instance) {
        AgreementInstanceDTO dto = new AgreementInstanceDTO();
        dto.setId(instance.getId());
        dto.setTemplateVersionId(instance.getTemplateVersion().getId());
        dto.setTemplateKey(instance.getTemplateKey());
        dto.setTemplateName(instance.getTemplateVersion().getTemplate().getDisplayName());
        dto.setVersionNumber(instance.getTemplateVersion().getVersionNumber());
        dto.setRecipientType(instance.getRecipientType());
        dto.setRecipientId(instance.getRecipientId());
        dto.setRecipientEmail(instance.getRecipientEmail());
        dto.setRecipientName(instance.getRecipientName());
        dto.setStatus(instance.getStatus());
        dto.setHasCustomContent(instance.getCustomContentHtml() != null && !instance.getCustomContentHtml().isBlank());
        dto.setHasAdminSignature(instance.getAdminSignatureId() != null);
        dto.setCreatedBy(instance.getCreatedBy());
        dto.setCreatedAt(instance.getCreatedAt());
        dto.setUpdatedAt(instance.getUpdatedAt());
        dto.setSentAt(instance.getSentAt());
        dto.setViewedAt(instance.getViewedAt());
        dto.setSignedAt(instance.getSignedAt());
        dto.setExpiresAt(instance.getExpiresAt());
        dto.setSentBy(instance.getSentBy());
        dto.setSendMethod(instance.getSendMethod());
        dto.setManualSendNotes(instance.getManualSendNotes());
        dto.setSignedViaUpload(Boolean.TRUE.equals(instance.getSignedViaUpload()));
        dto.setHasSignedPdf(instance.getSignedPdfPath() != null && !instance.getSignedPdfPath().isBlank());

        boolean hasAdminSig = instance.getAdminSignatureId() != null;
        boolean terminal = STATUS_SIGNED.equals(instance.getStatus()) || STATUS_CANCELLED.equals(instance.getStatus());
        boolean issued = instance.getSentAt() != null
                || STATUS_SENT.equals(instance.getStatus())
                || STATUS_VIEWED.equals(instance.getStatus())
                || STATUS_EXPIRED.equals(instance.getStatus());

        dto.setCanSend(hasAdminSig && !terminal && !issued
                && (STATUS_DRAFT.equals(instance.getStatus()) || STATUS_ADMIN_SIGNED.equals(instance.getStatus())));
        dto.setCanMarkManualSent(dto.isCanSend());
        dto.setCanCopySigningLink(hasAdminSig && !STATUS_SIGNED.equals(instance.getStatus())
                && !STATUS_CANCELLED.equals(instance.getStatus()));
        dto.setCanUploadSignedPdf(hasAdminSig && !STATUS_SIGNED.equals(instance.getStatus())
                && !STATUS_CANCELLED.equals(instance.getStatus()));
        dto.setCanClearAdminSignature(hasAdminSig && !STATUS_SIGNED.equals(instance.getStatus())
                && !STATUS_CANCELLED.equals(instance.getStatus()));
        dto.setCanUpdateStatus(!STATUS_CANCELLED.equals(instance.getStatus()));
        dto.setCanResend(
                STATUS_SENT.equals(instance.getStatus())
                        || STATUS_VIEWED.equals(instance.getStatus())
                        || STATUS_EXPIRED.equals(instance.getStatus()));
        dto.setCanCancel(!STATUS_SIGNED.equals(instance.getStatus())
                && !STATUS_CANCELLED.equals(instance.getStatus()));
        dto.setCanDelete(STATUS_CANCELLED.equals(instance.getStatus()));
        return dto;
    }

    private void assertReadyForFirstSend(AgreementInstance instance) {
        if (instance.getAdminSignatureId() == null) {
            throw new RuntimeException("Admin signature is required before sending");
        }
        if (STATUS_CANCELLED.equals(instance.getStatus()) || STATUS_SIGNED.equals(instance.getStatus())) {
            throw new RuntimeException("Agreement cannot be sent in status: " + instance.getStatus());
        }
        if (instance.getSentAt() != null
                || STATUS_SENT.equals(instance.getStatus())
                || STATUS_VIEWED.equals(instance.getStatus())
                || STATUS_EXPIRED.equals(instance.getStatus())) {
            throw new RuntimeException("Agreement was already sent. Use resend or regenerate link instead.");
        }
        if (!STATUS_DRAFT.equals(instance.getStatus()) && !STATUS_ADMIN_SIGNED.equals(instance.getStatus())) {
            throw new RuntimeException("Agreement cannot be sent in status: " + instance.getStatus());
        }
    }

    private String issueSigningToken(AgreementInstance instance, LocalDateTime expiresAt) {
        String plainToken = com.skycareSolutions.skyCarePortal.utils.AgreementSigningTokenUtil.generateToken();
        instance.setSigningTokenHash(
                com.skycareSolutions.skyCarePortal.utils.AgreementSigningTokenUtil.hashToken(plainToken));
        LocalDateTime expiry = expiresAt;
        if (expiry == null || expiry.isBefore(LocalDateTime.now())) {
            expiry = LocalDateTime.now().plusDays(DEFAULT_EXPIRY_DAYS);
        }
        instance.setExpiresAt(expiry);
        return plainToken;
    }

    private String buildSigningUrl(String plainToken) {
        String base = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;
        return base + "/sign/" + plainToken;
    }

    private AgreementSendResponseDTO buildSendResponse(
            AgreementInstance instance,
            String message,
            String plainToken
    ) {
        AgreementSendResponseDTO response = new AgreementSendResponseDTO();
        response.setInstance(toDto(instance));
        response.setExpiresAt(instance.getExpiresAt());
        response.setMessage(message);
        if (plainToken != null) {
            response.setSigningUrl(buildSigningUrl(plainToken));
        }
        return response;
    }

    private void deletePdfFile(String path) {
        if (path == null || path.isBlank()) {
            return;
        }
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    private AgreementEventDTO toEventDto(AgreementEvent event) {
        AgreementEventDTO dto = new AgreementEventDTO();
        dto.setId(event.getId());
        dto.setInstanceId(event.getInstanceId());
        dto.setEventType(event.getEventType());
        dto.setActorId(event.getActorId());
        dto.setActorType(event.getActorType());
        dto.setMetadata(event.getMetadata());
        dto.setCreatedAt(event.getCreatedAt());
        return dto;
    }

    private record RecipientInfo(String name, String email) {}

    public record PreparedAgreementSend(AgreementInstance instance, String plainToken) {}

    private String prepareHtmlForOutput(String html, AgreementInstance instance) {
        html = inlineAgreementAssetUrls(html);
        html = inlineOrganizationLogo(html, instance);
        return AgreementHtmlImageInliner.inlineImages(html, backendBaseUrl);
    }

    private String inlineAgreementAssetUrls(String html) {
        if (html == null) {
            return "";
        }
        String result = html;
        for (AgreementTemplateAsset asset : assetRepository.findAll()) {
            String url = backendBaseUrl + "/agreement-templates/assets/" + asset.getId();
            String dataUrl = AgreementHtmlImageInliner.fileToDataUrl(asset.getFilePath());
            if (!dataUrl.isBlank()) {
                result = result.replace(url, dataUrl);
            }
        }
        return result;
    }

    private String inlineOrganizationLogo(String html, AgreementInstance instance) {
        if (html == null) {
            return "";
        }
        var org = organizationSettingService.getEntity();
        boolean clientServiceAgreement = instance != null
                && "CLIENT".equalsIgnoreCase(instance.getRecipientType());

        if (clientServiceAgreement) {
            String dataUrl = AgreementLogoUtil.defaultLogoDataUrl();
            if (dataUrl.isBlank()) {
                return html;
            }
            String ndisLogoUrl = organizationSettingService.buildClientServiceAgreementLogoUrl();
            html = html.replace(ndisLogoUrl, dataUrl);
            String legacyOrgLogoUrl = organizationSettingService.buildLogoUrl(org);
            if (legacyOrgLogoUrl != null) {
                html = html.replace(legacyOrgLogoUrl, dataUrl);
            }
            html = html.replaceAll(
                    "(<img[^>]+src=\")[^\"]*?/agreements/default-logo[^\"]*(\")",
                    "$1" + Matcher.quoteReplacement(dataUrl) + "$2"
            );
            html = html.replaceAll(
                    "(<img[^>]+src=\")[^\"]*?/settings/organization/logo[^\"]*(\")",
                    "$1" + Matcher.quoteReplacement(dataUrl) + "$2"
            );
            return html;
        }

        String legacyOrgLogoUrl = organizationSettingService.buildLogoUrl(org);
        if (legacyOrgLogoUrl != null && org.getLogoPath() != null && !org.getLogoPath().isBlank()) {
            String dataUrl = AgreementHtmlImageInliner.fileToDataUrl(org.getLogoPath());
            if (!dataUrl.isBlank()) {
                html = html.replace(legacyOrgLogoUrl, dataUrl);
            }
        }
        return html;
    }

    private String formatCcSnapshot(java.util.List<String> ccEmails) {
        if (ccEmails == null || ccEmails.isEmpty()) {
            return null;
        }
        return ccEmails.stream()
                .filter(email -> email != null && !email.isBlank())
                .map(String::trim)
                .distinct()
                .reduce((left, right) -> left + "," + right)
                .orElse(null);
    }
}
