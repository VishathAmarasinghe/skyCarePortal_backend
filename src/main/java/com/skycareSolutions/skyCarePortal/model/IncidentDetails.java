package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "incidentDetails")
public class IncidentDetails {


    @EmbeddedId
    private IncidentDetailsID id;

    @Column(name = "state")
    private boolean state;

}
