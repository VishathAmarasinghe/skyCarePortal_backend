package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class AppointmentCalenderDTO {
    private String appointmentID;
    private String title;
    private String startDateAndTime;
    private String endDateAndTime;
    private String eventColor;
    private String recurrentAppointmentID;

}
