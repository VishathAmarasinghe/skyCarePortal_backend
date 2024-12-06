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
@Table(name = "incidentStatus")
public class IncidentStatus {

    @Id
    @Column(name = "incidentStatusID")
    private String incidentStatusID;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

    @Column(name = "activeState")
    private String activeState;
}
