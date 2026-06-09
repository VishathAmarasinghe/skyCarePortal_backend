package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgreementReferenceMaterialDTO {
    private String id;
    private String title;
    private String description;
    private String originalFilename;
    private String mimeType;
    private String audience;
    private String careGiverType;
    private int sortOrder;
    private boolean active;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
