package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class IncidentActionTypeAllAnswersDTO {
    private Long id; // ID of the answer
    private String incidentActionID; // Associated IncidentActionTypes ID
    private Long incidentSubActionID; // Associated IncidentSubActionTypes ID (nullable)
    private String incidentID; // Associated Incidents ID
    private String answer; // The answer text
}
