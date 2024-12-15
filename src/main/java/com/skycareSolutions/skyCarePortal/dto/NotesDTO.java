package com.skycareSolutions.skyCarePortal.dto;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class NotesDTO  {
    private String noteID;
    private String title;
    private String createDate;
    private String shiftStartTime;
    private String shiftEndTime;
    private String System_ShiftStartDate;
    private String System_ShiftStartTime;
    private String System_shift_endTime;
    private String System_shift_endDate;
    private String careplanID;
    private String clientID;
    private String noteType;
    private String taskID;
    private String appointmentID;
    private String effectiveDate;
    private String description;
    private String sharedGroup;
    private String createdBy;
    private List<NoteDocumentsDTO> documents;

}

