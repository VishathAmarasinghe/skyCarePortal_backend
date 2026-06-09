package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class TimesheetInsightsDTO {
    private String fromDate;
    private String toDate;
    private String previousFromDate;
    private String previousToDate;
    private long totalReceived;
    private long previousPeriodTotal;
    private Double changePercent;
    private double averagePerDay;
    private String peakDay;
    private long peakDayCount;
    private Map<String, Long> submissionsByDay = new LinkedHashMap<>();
}
