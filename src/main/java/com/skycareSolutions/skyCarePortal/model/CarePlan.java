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

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate", nullable = true)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "clientID", nullable = false )
    private Client clientID;
}
