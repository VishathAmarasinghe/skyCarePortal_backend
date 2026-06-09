package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "careplan")
public class CarePlan {

    @Id
    @Column(name = "careplanID", nullable = false, unique = true)
    private String careplanID;

    @ManyToOne
    @JoinColumn(name = "careplanStatusID", nullable = false)
    private CarePlanStatus carePlanStatus;

    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "startDate", nullable = false)
    private String startDate;

    @Column(name = "endDate", nullable = true)
    private String endDate;

    @ManyToOne
    @JoinColumn(name = "clientID", nullable = false )
    private Client clientID;

    @Column(name = "status")
    private String status;

    @Column(name = "currentLivingSituation", columnDefinition = "TEXT")
    private String currentLivingSituation;

    @Column(name = "supportsFundedCore", columnDefinition = "TEXT")
    private String supportsFundedCore;

    @Column(name = "supportsFundedCapacityBuilding", columnDefinition = "TEXT")
    private String supportsFundedCapacityBuilding;

    @Column(name = "supportsFundedCapital", columnDefinition = "TEXT")
    private String supportsFundedCapital;

    @Column(name = "riskManagement", columnDefinition = "TEXT")
    private String riskManagement;

    @Column(name = "expectedOutcomes", columnDefinition = "TEXT")
    private String expectedOutcomes;

    @Column(name = "billingBudgetNotes", columnDefinition = "TEXT")
    private String billingBudgetNotes;
}
