package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "behaviour_data_record")
public class BehaviourDataRecord {

    @Id
    @Column(name = "recordID")
    private String recordID;

    @ManyToOne
    @JoinColumn(name = "clientID", nullable = false)
    private Client client;

    @Column(name = "recordDate")
    private LocalDate recordDate;

    @Column(name = "recordTime")
    private LocalTime recordTime;

    @Column(name = "behaviourObserved", columnDefinition = "TEXT")
    private String behaviourObserved;

    @Column(name = "antecedent", columnDefinition = "TEXT")
    private String antecedent;

    @Column(name = "staffResponse", columnDefinition = "TEXT")
    private String staffResponse;

    @Column(name = "restrictivePracticeUsed")
    private String restrictivePracticeUsed;

    @Column(name = "outcome", columnDefinition = "TEXT")
    private String outcome;

    @Column(name = "duration")
    private String duration;

    @Column(name = "intensity")
    private String intensity;

    @Column(name = "followUpRequired")
    private boolean followUpRequired;

    @ManyToOne
    @JoinColumn(name = "staffEmployeeID", nullable = true)
    private Employee staffEmployee;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "createdBy")
    private String createdBy;
}
