package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class JobAssignCareGiverViewerDTO {
    private JobAssignerDTO assignJob;
    private RecurrentAppointmentDTO recurrentAppointment;
    private AppointmentDTO appointment;

}
