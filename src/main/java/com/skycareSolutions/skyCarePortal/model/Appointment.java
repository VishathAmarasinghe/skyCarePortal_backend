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

    @ManyToOne
    @JoinColumn(name = "appointmentTypeID", nullable = false)
    private AppointmentType appointmentType;

    @Column(name = "clientID", nullable = false)
    private String clientID;

    @Column(name = "caregiverCount", nullable = false)
    private int caregiverCount;

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "startTime")
    private LocalDateTime startTime;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "endTime")
    private LocalDateTime endTime;

    @Column(name = "duration")
    private int duration; // Assuming duration is in minutes

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "carePlanID")
    private CarePlan carePlan;

    @ManyToOne
    @JoinColumn(name = "taskID")
    private Task task;

    @Column(name = "broadcastType", nullable = false)
    private String broadcastType;

    @ManyToOne
    @JoinColumn(name = "appointmentAddressID")
    private AppointmentAddress appointmentAddress;
}
