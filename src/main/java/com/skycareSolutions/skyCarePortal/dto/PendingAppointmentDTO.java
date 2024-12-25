package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class PendingAppointmentDTO {
    private String recurrentAppointmentID;
    private String careGiverStatus;
    private AppointmentDTO appointmentData;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
}
