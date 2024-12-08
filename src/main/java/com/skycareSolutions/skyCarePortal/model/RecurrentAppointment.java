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
@Table(name = "recurrentAppointment")
public class RecurrentAppointment {

    @Id
    @Column(name = "recurrentAppointmentID", nullable = false, unique = true)
    private String recurrentAppointmentID;

    @ManyToOne
    @JoinColumn(name = "appointmentID", nullable = false)
    private Appointment appointment;  // Link to the original appointment

    @ManyToOne
    @JoinColumn(name = "recurrentWorkID", nullable = false)
    private RecurrentWork recurrentWork;  // Link to the recurrence pattern

    @Column(name = "occurrenceNumber", nullable = false)
    private int occurrenceNumber;  // Which occurrence this is (1st, 2nd, etc.)

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;  // The start date of this occurrence

    @Column(name = "startTime", nullable = false)
    private String startTime;  // The start time of this occurrence

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;  // The end date of this occurrence

    @Column(name = "endTime", nullable = false)
    private String endTime;  // The end time of this occurrence

    @Column(name = "status", nullable = false)
    private String status;  // Status of the specific recurrent appointment (Scheduled, Completed, etc.)

    @Column(name = "isCancelled", nullable = false)
    private boolean isCancelled;  // Indicates if the appointment was cancelled

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
}
