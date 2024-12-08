package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class CareGiverAppointmentDTO {
    private String careGiverID;
    private AppointmentDTO appointmentData;
    private JobAssignerDTO jobAssignData;

}
