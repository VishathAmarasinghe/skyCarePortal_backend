package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class CareGiverAllocationResponseDTO {
    private RecurrentAppointmentDTO recurrentAppointmentData;
    private JobAssignerDTO jobAssignData;
    private int totalRequired;
    private int pendingRequired;

}
