package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.List;

@Data
public class PendingClientAppointmentDTO {
    private String recurrentAppointmentID;
    private AppointmentDTO appointmentData;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private List<String> careGiverNames;
}
