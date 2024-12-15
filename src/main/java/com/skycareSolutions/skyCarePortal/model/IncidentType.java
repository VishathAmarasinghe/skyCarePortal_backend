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
@Table(name = "incidentType")
public class IncidentType {

    @Id
    @Column(name = "incidentTypeID")
    private String incidentTypeID;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    private String status;
}
