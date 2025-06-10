package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class NextAppointmentCareGiverDTO {
    private AppointmentDTO appointment;
    private RecurrentAppointmentDTO recurrentTask;
    private String employeeName;
}
