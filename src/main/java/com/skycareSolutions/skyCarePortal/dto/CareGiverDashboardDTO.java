package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CareGiverDashboardDTO {
    private Map<String,Long> twoWeekAppointmentCount;
}
