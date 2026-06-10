package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgreementTemplateUpdateDTO {
    private String displayName;
    private String audience;
    private String careGiverType;
    private Boolean defaultForAudience;
    private String status;
}
