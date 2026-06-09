package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgreementTemplateVersionSummaryDTO {
    private String id;
    private int versionNumber;
    private String status;
    private String changeNotes;
    private LocalDateTime publishedAt;
    private String publishedBy;
    private LocalDateTime createdAt;
}
