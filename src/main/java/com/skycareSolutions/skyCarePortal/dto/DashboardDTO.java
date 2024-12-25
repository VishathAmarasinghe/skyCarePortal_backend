package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardDTO {

    private int clientCount;
    private int serviceCount;
    private int staffCount;
    private int adminCount;
    private int todayTotalCount;
    private int todayCompletedCount;
    private List<AppointmentCalenderDTO> todayAppointments;
    private Map<String,Long> twoWeekAppointmentCount;
    private Map<String,Integer> appointmentCountByType;
//    private
}
