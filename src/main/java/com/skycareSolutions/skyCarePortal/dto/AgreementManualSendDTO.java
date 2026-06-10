package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AgreementManualSendDTO {
    /** MANUAL_LINK or MANUAL_PDF */
    private String sendMethod;
    private String notes;
    private LocalDateTime expiresAt;
    private String actorId;
    private List<String> referenceMaterialIds;
}
