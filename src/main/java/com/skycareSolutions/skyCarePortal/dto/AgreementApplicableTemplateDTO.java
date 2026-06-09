package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgreementApplicableTemplateDTO {
    private String templateId;
    private String templateKey;
    private String displayName;
    private String publishedVersionId;
    private int publishedVersionNumber;
    private String careGiverType;
    private boolean defaultForAudience;
}
