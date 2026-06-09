package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgreementInstanceCreateDTO {
    private String templateVersionId;
    private String templateKey;
    private String recipientType;
    private String recipientId;
    private String createdBy;
}
