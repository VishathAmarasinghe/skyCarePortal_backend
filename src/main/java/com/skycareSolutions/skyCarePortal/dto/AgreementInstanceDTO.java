package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgreementInstanceDTO {
    private String id;
    private String templateVersionId;
    private String templateKey;
    private String templateName;
    private int versionNumber;
    private String recipientType;
    private String recipientId;
    private String recipientEmail;
    private String recipientName;
    private String status;
    private boolean hasCustomContent;
    private boolean hasAdminSignature;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;
    private LocalDateTime viewedAt;
    private LocalDateTime signedAt;
    private LocalDateTime expiresAt;
    private String sentBy;
    private String sendMethod;
    private String manualSendNotes;
    private boolean signedViaUpload;
    private boolean hasSignedPdf;
    private boolean canSend;
    private boolean canResend;
    private boolean canCancel;
    private boolean canDelete;
    private boolean canMarkManualSent;
    private boolean canCopySigningLink;
    private boolean canUploadSignedPdf;
    private boolean canClearAdminSignature;
    private boolean canUpdateStatus;
}
