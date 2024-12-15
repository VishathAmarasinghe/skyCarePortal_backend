package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardDTO {

    private int clientCount;
    private int serviceCount;
    private int staffCount;
    private int adminCount;
    private List<AppointmentCalenderDTO> todayAppointments;
//    private
}
