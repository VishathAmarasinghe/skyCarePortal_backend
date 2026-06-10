package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AgreementPublicDocumentDTO {
    private String templateName;
    private int versionNumber;
    private String recipientName;
    private String organizationName;
    private String mergedHtml;
    private String status;
    private LocalDateTime expiresAt;
    private boolean alreadySigned;
    private String consentText;
    private boolean expired;
    private boolean cancelled;
    private boolean emailVerificationRequired;
    private List<AgreementReferenceMaterialSummaryDTO> referenceMaterials;
}
