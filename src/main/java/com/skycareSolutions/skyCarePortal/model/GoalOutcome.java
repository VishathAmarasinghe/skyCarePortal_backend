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
@Table(name = "goalOutcome")
public class GoalOutcome {

    @Id
    @Column(name = "goalOutcomeID", nullable = false, unique = true)
    private String goalOutcomeID;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
