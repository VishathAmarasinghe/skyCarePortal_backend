package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MobileAdminDashboardDTO {

    private int clientCount;
    private int serviceCount;
    private int staffCount;
    private int adminCount;
    private int todayTotalCount;
    private int todayCompletedCount;
    private int monthlyTimesheetSubmissionCount;
    private int monthlyApprovedTimesheetCount;
    private int monthlyPendingTimesheetCount;
    private int monthlyRejectedTimesheetCount;
    private int monthlyPaidTimesheetCount;
    private List<AppointmentCalenderDTO> todayAppointments;
    private Map<String,Long> twoWeekAppointmentCount;
    private Map<String,Integer> appointmentCountByType;
    private Map<String,Long> twoWeekTimeSheetSubmissionCount;
}
