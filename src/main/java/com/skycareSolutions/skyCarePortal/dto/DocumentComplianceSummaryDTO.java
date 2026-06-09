package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class DocumentComplianceSummaryDTO {
    private int expired;
    private int expiringIn7Days;
    private int expiringIn30Days;
    private int missingRequired;
    private int pending;
    private int missingExpiryDate;
}
