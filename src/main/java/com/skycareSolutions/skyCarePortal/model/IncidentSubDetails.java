package com.skycareSolutions.skyCarePortal.model;


import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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
@Table(name = "IncidentSubDetails")
public class IncidentSubDetails {

    @EmbeddedId
    private IncidentSubDetailsID id;

    @Column(name = "incidentText")
    private String text;
}
