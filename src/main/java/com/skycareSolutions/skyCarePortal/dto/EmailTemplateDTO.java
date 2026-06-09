package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailTemplateDTO {
    private String templateID;
    private String title;
    private String content;
    private String category;
    private String placeholdersMetadata;
}
