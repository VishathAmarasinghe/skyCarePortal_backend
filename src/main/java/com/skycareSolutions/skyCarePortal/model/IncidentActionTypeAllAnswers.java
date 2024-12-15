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
@Table(name = "IncidentActionTypeAllAnswers")
public class IncidentActionTypeAllAnswers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key for IncidentActionTypes
    @ManyToOne
    @JoinColumn(name = "incidentActionID", nullable = false)
    private IncidentActionTypes incidentActionTypes;

    // Foreign key for IncidentSubActionTypes (nullable)
    @ManyToOne
    @JoinColumn(name = "incidentSubActionID", nullable = true)
    private IncidentSubActionTypes incidentSubActionTypes;

    @Column(name = "answer", nullable = false)
    private String answer;

    @ManyToOne
    @JoinColumn(name = "incidentID")
    private Incidents incidents;
}
