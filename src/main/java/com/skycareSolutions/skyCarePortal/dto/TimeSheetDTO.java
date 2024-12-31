package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class TimeSheetDTO {
    private ClientDTO client;
    private EmployeeDTO employeeDTO;
    private ShiftNoteDTO shiftNoteDTO;
    private String totalHours;
    private RecurrentAppointmentDTO recurrentAppointment;
    private AppointmentDTO appointment;
}
