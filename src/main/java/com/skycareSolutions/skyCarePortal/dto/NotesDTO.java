package com.skycareSolutions.skyCarePortal.dto;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class NotesDTO extends NoteDocumentsDTO {
    private String noteID;
    private String title;
    private String createDate;
    private String shiftStartTime;
    private String shiftEndTime;
    private String careplanID;
    private String clientID;
    private String noteType;
    private String taskID;
    private String appointmentID;
    private String effectiveDate;
    private String description;
    private String sharedGroup;
    private String createdBy;

}

