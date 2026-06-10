package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgreementTemplateCreateDTO {
    private String templateKey;
    private String displayName;
    private String audience;
    private String careGiverType;
    private boolean defaultForAudience;
    private String initialContentHtml;
    private String changeNotes;
}
