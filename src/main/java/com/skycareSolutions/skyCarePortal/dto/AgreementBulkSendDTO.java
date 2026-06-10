package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AgreementBulkSendDTO {
    /** WORKER or CLIENT. Defaults to WORKER when omitted. */
    private String recipientType;
    /** Recipient IDs (care giver IDs for workers, client IDs for clients). */
    private List<String> recipientIds;
    /** @deprecated use recipientIds with recipientType WORKER */
    private List<String> careGiverIds;
    private String templateKey;
    private String emailTemplateId;
    private String subjectOverride;
    private String bodyHtmlOverride;
    private LocalDateTime expiresAt;
    private String sentBy;
    private String adminEmployeeId;
}
