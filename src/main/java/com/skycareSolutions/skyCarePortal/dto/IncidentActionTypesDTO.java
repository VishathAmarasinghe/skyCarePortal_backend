package com.skycareSolutions.skyCarePortal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentActionTypesDTO {
    private String incidentActionID;
    private String question;
    private boolean answer;
}
