package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailTemplateTestSendDTO {
    private String recipientEmail;
    private String subject;
    private String content;
}
