package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class RecurrentAppointmentUpdateDTO {
    private String recurrentAppointmentID;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String comment;
}
