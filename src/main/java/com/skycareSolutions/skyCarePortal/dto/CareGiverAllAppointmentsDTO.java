package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class CareGiverAllAppointmentsDTO {
    private RecurrentAppointmentDTO recurrentAppointment;
    private AppointmentDTO appointment;
    private JobAssignerDTO jobAssign;
}
