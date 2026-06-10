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
@Table(name = "restrictive_practice_register")
public class RestrictivePracticeRegister {

    @Id
    @Column(name = "registerID")
    private String registerID;

    @ManyToOne
    @JoinColumn(name = "clientID", nullable = false)
    private Client client;

    @Column(name = "ndisNumber")
    private String ndisNumber;

    @Column(name = "restrictivePracticeType")
    private String restrictivePracticeType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "behaviourSupportPlanCurrent")
    private boolean behaviourSupportPlanCurrent;

    @Column(name = "authorisationStatus")
    private String authorisationStatus;

    @Column(name = "authorisingBody")
    private String authorisingBody;

    @Column(name = "dateAuthorised")
    private LocalDate dateAuthorised;

    @Column(name = "reviewDate")
    private LocalDate reviewDate;

    @ManyToOne
    @JoinColumn(name = "practitionerEmployeeID")
    private Employee practitionerEmployee;

    @Column(name = "reductionStrategyInPlace")
    private boolean reductionStrategyInPlace;

    @Column(name = "incidentIds", columnDefinition = "TEXT")
    private String incidentIds;

    @Column(name = "staffTrained")
    private boolean staffTrained;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "createdBy")
    private String createdBy;
}
