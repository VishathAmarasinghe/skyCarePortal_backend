package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgreementTemplateAssignmentDTO {
    private String id;
    private String templateId;
    private String templateKey;
    private String templateDisplayName;
    private String ruleType;
    private String careGiverType;
    private int priority;
}
