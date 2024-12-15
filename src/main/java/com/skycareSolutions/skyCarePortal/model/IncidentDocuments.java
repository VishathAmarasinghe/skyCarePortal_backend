package com.skycareSolutions.skyCarePortal.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "incident_documents")
public class IncidentDocuments {

    @Id
    @Column(name = "document_id", nullable = false, unique = true)
    private String documentID;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "document_location", nullable = false)
    private String documentLocation;

    @ManyToOne
    @JoinColumn(name = "incidentID", nullable = false)
    private Incidents incidents;
}
