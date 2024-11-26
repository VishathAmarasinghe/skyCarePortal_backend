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
@Table(name = "appointment")
public class Appointment {

    @Id
    @Column(name = "appointmentID", nullable = false, unique = true)
    private String appointmentID;

    @Column(name = "title",nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "appointmentTypeID", nullable = false)
    private AppointmentType appointmentType;

    @ManyToOne
    @JoinColumn(name = "clientID", nullable = false)
    private Client clientID;

    @Column(name = "caregiverCount", nullable = false)
    private int caregiverCount;

    @Column(name = "startDate", nullable = false)
    private String startDate;

    @Column(name = "startTime")
    private String startTime;

    @Column(name = "endDate", nullable = false)
    private String endDate;

    @Column(name = "endTime")
    private String endTime;

    @Column(name = "duration")
    private int duration; // Assuming duration is in minutes

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "carePlanID",nullable = true)
    private CarePlan carePlan;

    @ManyToOne
    @JoinColumn(name = "taskID",nullable = true)
    private Task task;

    @Column(name = "broadcastType", nullable = false)
    private String broadcastType;

    @ManyToOne
    @JoinColumn(name = "appointmentAddressID")
    private AppointmentAddress appointmentAddress;
}
