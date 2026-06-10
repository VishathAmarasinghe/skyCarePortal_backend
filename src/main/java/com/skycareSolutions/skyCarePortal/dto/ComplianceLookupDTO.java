package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class ComplianceLookupDTO {
    private String lookupID;
    private String lookupType;
    private String title;
    private String description;
    private String status;
    private Integer sortOrder;
}
