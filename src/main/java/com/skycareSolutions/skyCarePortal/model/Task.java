package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task")
public class Task {

    @Id
    @Column(name = "taskID", nullable = false, unique = true)
    private String taskID;

    @ManyToOne
    @JoinColumn(name = "clientID", nullable = false)
    private Client client;  // Foreign key to Client

    @Column(name = "careGiverCount")
    private int careGiverCount;

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "startTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "endTime", nullable = false)
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "carePlanID", nullable = false)
    private CarePlan carePlan;  // Foreign key to CarePlan

    @Column(name = "duration", nullable = false)
    private int duration;  // Duration in minutes or hours

    @Column(name = "comment")
    private String comment;

    @Column(name = "broadcastType")
    private String broadcastType;

    @ManyToOne
    @JoinColumn(name = "taskStatusID", nullable = false)
    private TaskStatus taskStatus;  // Foreign key to TaskStatus

    @ManyToOne
    @JoinColumn(name = "taskImportanceID", nullable = false)
    private TaskImportance taskImportance;  // Foreign key to TaskImportance

    @ManyToOne
    @JoinColumn(name = "appointmentAddressID")
    private AppointmentAddress appointmentAddress;
}
