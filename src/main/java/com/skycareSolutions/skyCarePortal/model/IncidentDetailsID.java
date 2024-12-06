package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class IncidentDetailsID {

    @ManyToOne
    @JoinColumn(name = "incidentID")
    private Incidents incidents;

    @ManyToOne
    @JoinColumn(name = "actionID")
    private IncidentActionTypes incidentActionTypes;

    public IncidentDetailsID(Incidents incidents, IncidentActionTypes incidentActionTypes) {
        this.incidents = incidents;
        this.incidentActionTypes = incidentActionTypes;
    }
}
