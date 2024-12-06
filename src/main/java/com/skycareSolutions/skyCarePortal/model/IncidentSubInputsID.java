package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class IncidentSubInputsID implements Serializable {

    @Column(name = "fieldName", nullable = false)
    private String fieldName;

    @Column(name = "incidentActionID", nullable = false)
    private String incidentActionID;
}
