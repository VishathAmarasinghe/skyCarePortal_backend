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
@Table(name = "recurrentWork")
public class RecurrentWork {

    @Id
    @Column(name = "recurrentWorkID", nullable = false, unique = true)
    private String recurrentWorkID;

    @ManyToOne
    @JoinColumn(name = "appointmentID")
    private Appointment appointment;  // Foreign key to original appointment

    @ManyToOne
    @JoinColumn(name = "taskID")
    private Task task;  // Foreign key to associated task

    @Column(name = "recurrenceType", nullable = false)
    private String recurrenceType;  // (e.g., DAILY, WEEKLY, MONTHLY)

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate")
    private LocalDate endDate;

    @Column(name = "frequencyCount")
    private int frequencyCount;  // (e.g., every 2 days, every 3 weeks)

    @Column(name = "day")
    private String day;  // (e.g., "Monday" for weekly recurrence)

    @Column(name = "occurrenceLimit")
    private int occurrenceLimit;  // Maximum number of occurrences
}
