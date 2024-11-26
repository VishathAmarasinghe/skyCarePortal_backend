package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "jobAssigner")
public class JobAssigner {

    @Id
    @Column(name = "assignID", nullable = false, unique = true)
    private String assignID; // Primary Key (not auto-generated)

    @ManyToOne
    @JoinColumn(name = "careGiverID", nullable = false)
    private CareGiver careGiver; // Foreign key to CareGiver

    @ManyToOne
    @JoinColumn(name = "taskID", nullable = true)
    private Task task; // Foreign key to Task (nullable if not associated with a task)

    @ManyToOne
    @JoinColumn(name = "appointmentID", nullable = true)
    private Appointment appointment; // Foreign key to Appointment (nullable if not associated with an appointment)

    @ManyToOne
    @JoinColumn(name = "assignerID",nullable = false)
    private Employee employee;

    @Column(name = "assignType", nullable = false)
    private String assignType; // Type of assignment (e.g., Task, Appointment)

    @Column(name = "assignTime")
    private Timestamp assignTime;

    @Column(name = "acceptanceType", nullable = false)
    private String acceptanceType; // Acceptance status (e.g., Pending, Accepted, Rejected)
}
