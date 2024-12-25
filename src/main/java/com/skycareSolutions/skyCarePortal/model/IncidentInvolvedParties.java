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

    @Id
    @Column(name = "partyID")
    private String partyID;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "workPhoneNo")
    private String workPhoneNo;

    @Column(name = "otherPhoneNo")
    private String otherPhoneNo;

    @ManyToOne
    @JoinColumn(name = "incidentID")
    private Incidents incidents;

    @Column(name = "type")
    private String type;

}
