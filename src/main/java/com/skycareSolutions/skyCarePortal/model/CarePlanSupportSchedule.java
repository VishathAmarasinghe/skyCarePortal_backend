package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "careplanSupportSchedule")
public class CarePlanSupportSchedule {

    @Id
    @Column(name = "scheduleID", nullable = false, unique = true)
    private String scheduleID;

    @ManyToOne
    @JoinColumn(name = "careplanID", nullable = false)
    private CarePlan careplanID;

    @Column(name = "day", nullable = false)
    private String day;

    @Column(name = "supportType", columnDefinition = "TEXT")
    private String supportType;

    @Column(name = "hours")
    private double hours;

    @Column(name = "rate")
    private double rate;
}

