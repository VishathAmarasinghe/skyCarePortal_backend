package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "agreement_instance")
public class AgreementInstance {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_version_id", nullable = false)
    private AgreementTemplateVersion templateVersion;

    @Column(name = "template_key")
    private String templateKey;

    @Column(name = "recipient_type", nullable = false)
    private String recipientType;

    @Column(name = "recipient_id", nullable = false)
    private String recipientId;

    @Column(name = "recipient_email")
    private String recipientEmail;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "base_merged_html", columnDefinition = "LONGTEXT")
    private String baseMergedHtml;

    @Column(name = "custom_content_html", columnDefinition = "LONGTEXT")
    private String customContentHtml;

    @Column(name = "final_html_snapshot", columnDefinition = "LONGTEXT")
    private String finalHtmlSnapshot;

    @Column(name = "unsigned_pdf_path")
    private String unsignedPdfPath;

    @Column(name = "admin_signed_pdf_path")
    private String adminSignedPdfPath;

    @Column(name = "admin_signature_id")
    private String adminSignatureId;

    @Column(name = "recipient_signature_id")
    private String recipientSignatureId;

    @Column(name = "signing_token_hash", length = 128)
    private String signingTokenHash;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "signed_pdf_path")
    private String signedPdfPath;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "sent_by")
    private String sentBy;

    @Column(name = "email_template_id")
    private String emailTemplateId;

    @Column(name = "email_subject_snapshot")
    private String emailSubjectSnapshot;

    @Column(name = "email_body_snapshot", columnDefinition = "LONGTEXT")
    private String emailBodySnapshot;

    @Column(name = "email_cc_snapshot", columnDefinition = "TEXT")
    private String emailCcSnapshot;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_reminder_at")
    private LocalDateTime lastReminderAt;

    @Column(name = "reminder_count")
    private Integer reminderCount;

    @Column(name = "send_method", length = 32)
    private String sendMethod;

    @Column(name = "manual_send_notes", columnDefinition = "TEXT")
    private String manualSendNotes;

    @Column(name = "signed_via_upload")
    private Boolean signedViaUpload;
}
