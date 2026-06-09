package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "restrictive_practice_evidence")
public class RestrictivePracticeEvidence {

    @Id
    @Column(name = "evidenceID")
    private String evidenceID;

    @ManyToOne
    @JoinColumn(name = "clientID", nullable = false)
    private Client client;

    @Column(name = "restrictivePracticeType")
    private String restrictivePracticeType;

    @Column(name = "baselineFrequency")
    private String baselineFrequency;

    @Column(name = "currentFrequency")
    private String currentFrequency;

    @Column(name = "reductionGoal", columnDefinition = "TEXT")
    private String reductionGoal;

    @Column(name = "strategiesImplemented", columnDefinition = "TEXT")
    private String strategiesImplemented;

    @Column(name = "alternativeSupportsUsed", columnDefinition = "TEXT")
    private String alternativeSupportsUsed;

    @Column(name = "staffTrainingCompleted")
    private boolean staffTrainingCompleted;

    @Column(name = "behaviourDataReviewed")
    private boolean behaviourDataReviewed;

    @Column(name = "reviewDate")
    private LocalDate reviewDate;

    @Column(name = "outcomeProgress", columnDefinition = "TEXT")
    private String outcomeProgress;

    @Column(name = "nextActions", columnDefinition = "TEXT")
    private String nextActions;

    @ManyToOne
    @JoinColumn(name = "responsibleEmployeeID")
    private Employee responsibleEmployee;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "createdBy")
    private String createdBy;
}
