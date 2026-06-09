package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class DocumentComplianceItemDTO {
    private String employeeId;
    private String careGiverId;
    private String employeeName;
    private String careGiverType;
    private String documentTypeId;
    private String documentTypeName;
    private String status;
    private String expDate;
    private Integer daysUntilExpiry;
    private String issueType;
    private boolean required;
}
