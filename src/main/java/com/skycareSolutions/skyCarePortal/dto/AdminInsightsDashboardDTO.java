package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class AdminInsightsDashboardDTO {
    private int staffCount;
    private int clientCount;
    private TimesheetInsightsDTO timesheet;
    private DocumentComplianceInsightsDTO documents;
    private AgreementAttentionDTO agreements;
}
