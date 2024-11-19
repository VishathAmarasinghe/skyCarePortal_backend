package com.skycareSolutions.skyCarePortal.dto;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class NotesDTO extends NoteDocumentsDTO {
    private String noteID;
    private String title;
    private LocalDate createDate;
    private LocalDateTime shiftStartTime;
    private LocalDateTime shiftEndTime;
    private String careplanID;
    private String clientID;
    private String noteType;
    private String taskID;
    private String appointmentID;
    private LocalDate effectiveDate;
    private String description;
    private String sharedGroup;
    private String createdBy;

}

