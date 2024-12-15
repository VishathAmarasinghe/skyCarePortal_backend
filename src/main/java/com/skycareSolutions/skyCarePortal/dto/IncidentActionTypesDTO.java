package com.skycareSolutions.skyCarePortal.dto;

import com.skycareSolutions.skyCarePortal.model.IncidentActionTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentActionTypesDTO {
    private String incidentActionID;
    private String question;
    private boolean yesNoAnswer;
    private String status;
    private List<IncidentSubActionTypesDTO> incidentSubActionList;
}
