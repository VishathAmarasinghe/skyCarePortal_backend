package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CareGiverDocumentsDTO {
    private String careGiverID;
    private String documentTypeID;
    private Date expDate;
    private String status;
    private String document;
}
