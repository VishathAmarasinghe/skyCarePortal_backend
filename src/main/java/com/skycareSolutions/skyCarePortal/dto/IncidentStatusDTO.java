package com.skycareSolutions.skyCarePortal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentStatusDTO {
    private String incidentStatusID;
    private String status;
    private String description;
    private String activeState;
}
