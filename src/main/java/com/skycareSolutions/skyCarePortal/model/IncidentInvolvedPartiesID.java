package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class IncidentInvolvedPartiesID {

    @Column(name = "incidentID")
    private String incidentID;

    @Column(name = "employeeID")
    private String employeeID;

    public IncidentInvolvedPartiesID(String employeeID, String incidentID) {
        this.employeeID = employeeID;
        this.incidentID = incidentID;
    }
}
