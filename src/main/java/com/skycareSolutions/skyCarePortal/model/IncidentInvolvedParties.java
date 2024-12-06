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
@Table(name = "incidentInvolvedParties")
public class IncidentInvolvedParties {

    @EmbeddedId
    private IncidentSubInputsID id;

    @Column(name = "type")
    private String type;

}
