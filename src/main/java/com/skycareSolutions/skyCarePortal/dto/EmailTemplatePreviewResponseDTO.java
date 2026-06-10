package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailTemplatePreviewResponseDTO {
    private String mergedSubject;
    private String mergedBodyHtml;
    private String fullHtml;
    private String fromAddress;
    private String sampleRecipient;
}
