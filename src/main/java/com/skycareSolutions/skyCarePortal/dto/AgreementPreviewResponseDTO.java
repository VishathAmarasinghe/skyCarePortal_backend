package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgreementPreviewResponseDTO {
    private String mergedHtml;
    private String templateKey;
    private int versionNumber;
}
