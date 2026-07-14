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
@Table(name = "carePlanShortTermGoal")
public class CarePlanShortTermGoal {

    @Id
    @Column(name = "shortTermGoalID", nullable = false, unique = true)
    private String shortTermGoalID;

    @ManyToOne
    @JoinColumn(name = "longTermGoalID", nullable = false)
    private CarePlanLongTermGoal longTermGoalID;

    @Column(name = "goalTitle", nullable = false, columnDefinition = "TEXT")
    private String goalTitle;

    @Column(name = "goalDescription", columnDefinition = "TEXT")
    private String goalDescription;

    @ManyToOne
    @JoinColumn(name = "goalOutcomeTypeID", nullable = false)
    private GoalOutcome goalOutcome;

    @Column(name = "outcomeDetails", columnDefinition = "TEXT")
    private String outcomeDetails;

    @Column(name = "goalStrategy", columnDefinition = "TEXT")
    private String goalStrategy;

    @ManyToOne
    @JoinColumn(name = "careplanID", nullable = false)
    private CarePlan carePlanID;
}
