package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "IncidentActionTypes")
public class IncidentActionTypes {

    @Id
    @Column(name = "incidentActionID")
    private String incidentActionID;

    @Column(name = "question")
    private String question;

    @Column(name = "yesNoType")
    private boolean yesNoType;

    @Column(name = "status")
    private String status;
}
