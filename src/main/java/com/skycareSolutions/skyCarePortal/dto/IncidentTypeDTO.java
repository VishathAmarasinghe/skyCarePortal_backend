package com.skycareSolutions.skyCarePortal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentTypeDTO {
    private String incidentTypeID;
    private String title;
    private String description;
}
