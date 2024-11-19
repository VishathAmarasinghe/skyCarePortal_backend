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
@Table(name = "carePlanLongTermGoal")
public class CarePlanLongTermGoal {

    @Id
    @Column(name = "longGoalID", nullable = false, unique = true)
    private String longGoalID;

    @Column(name = "longTermGoal", nullable = false)
    private String longTermGoal;

    @Column(name = "achieveWay")
    private String achieveWay;

    @Column(name = "supportWay")
    private String supportWay;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "careplanID", nullable = false)
    private CarePlan carePlan;
}
