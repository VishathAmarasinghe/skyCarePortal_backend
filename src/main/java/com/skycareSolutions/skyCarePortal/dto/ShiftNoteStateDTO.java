package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class ShiftNoteStateDTO {
    private String shiftNoteID;
    private String recurrentAppointmentID;
    private String employeeID;
    private String CareGiverID;
    private String ShiftNoteState;
    private String shiftNoteAvailability;
}
