package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class MobileDashboardDTO {
    private long pendingAppointmentCount;
    private long completedAppointmentCount;
}
