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
@Table(name = "IncidentSubActionTypes")
public class IncidentSubActionTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question")
    private String question;

    @ManyToOne
    @JoinColumn(name = "incidentActionID")
    private IncidentActionTypes incidentActionTypes;

    @Column(name = "state")
    private String state;


}
