package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AgreementSendDTO {
    private String emailTemplateId;
    private String recipientEmail;
    private String subjectOverride;
    private String bodyHtmlOverride;
    private LocalDateTime expiresAt;
    private String sentBy;
    private List<String> ccEmails;
    private List<String> referenceMaterialIds;
}
