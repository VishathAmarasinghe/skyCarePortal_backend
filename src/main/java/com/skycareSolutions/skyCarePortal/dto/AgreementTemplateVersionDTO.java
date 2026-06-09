package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgreementTemplateVersionDTO {
    private String id;
    private String templateId;
    private String templateKey;
    private int versionNumber;
    private String status;
    private String contentHtml;
    private String changeNotes;
    private LocalDateTime publishedAt;
    private String publishedBy;
    private LocalDateTime createdAt;
}
