package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ShiftNoteDTO {
    private String noteID;
    private String title;
    private LocalDate shiftStartDate;
    private String shiftStartTime;
    private LocalDate shiftEndDate;
    private String shiftEndTime;
    private LocalDate systemShiftStartDate;
    private LocalDate systemShiftEndDate;
    private String systemShiftStartTime;
    private String systemShiftEndTime;
    private String recurrentAppointmentID;
    private String notes;
    private String comments;
    private String employeeID;
    private String careGiverID;
    private double totalWorkHrs;
    private String state;
    private String clientID;
    private List<ShiftNoteDocumentDTO> documents;
    private String paymentState;
    private String createdAt;
}
