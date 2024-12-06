package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class IncidentSubDetailsID {


    @Column(name = "incidentID")
    private String incidentID;

    @Column(name = "actionID")
    private String actionID;

    @Column(name = "fieldName")
    private String fieldName;

    public IncidentSubDetailsID(String incidentID, String actionID, String fieldName) {
        this.incidentID = incidentID;
        this.actionID = actionID;
        this.fieldName = fieldName;
    }
}
