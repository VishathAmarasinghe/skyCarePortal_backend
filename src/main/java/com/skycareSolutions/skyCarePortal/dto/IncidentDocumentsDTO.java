package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class IncidentDocumentsDTO {
    private String documentID; // Unique ID for the document

    private String documentName; // Name of the document

    private String documentLocation; // Location or path of the document

    private String incidentID; // Associated Incident ID
}
