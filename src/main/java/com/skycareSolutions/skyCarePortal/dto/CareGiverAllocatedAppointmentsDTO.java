package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.List;

@Data
public class CareGiverAllocatedAppointmentsDTO {
    private String careGiverID;
    private AppointmentDTO appointmentData;
    private List<CareGiverAllocationResponseDTO> allocations;
}
