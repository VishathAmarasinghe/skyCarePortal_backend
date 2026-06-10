package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EmailTemplatePreviewRequestDTO {
    private String subject;
    private String bodyHtml;
    private String sampleRecipientName;
    private String sampleRecipientEmail;
    private Map<String, String> tokenOverrides;
}
