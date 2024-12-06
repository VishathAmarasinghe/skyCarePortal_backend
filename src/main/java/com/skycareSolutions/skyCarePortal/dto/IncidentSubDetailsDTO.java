package com.skycareSolutions.skyCarePortal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentSubDetailsDTO {
    private String incidentID;
    private String actionID;
    private String fieldName;
    private String text;
}
