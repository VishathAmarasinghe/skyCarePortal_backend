package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AgreementTemplateDTO {
    private String id;
    private String templateKey;
    private String displayName;
    private String audience;
    private String careGiverType;
    private boolean defaultForAudience;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer publishedVersionNumber;
    private String publishedVersionId;
    private List<AgreementTemplateVersionSummaryDTO> versions;
}
