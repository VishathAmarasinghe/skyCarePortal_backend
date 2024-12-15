package com.skycareSolutions.skyCarePortal.dto;

import com.skycareSolutions.skyCarePortal.model.IncidentActionTypes;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class IncidentSubActionTypesDTO {

    private Long id;
    private String question;
    private String incidentActionTypesID;
    private String state;

}
