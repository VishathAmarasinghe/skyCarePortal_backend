package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "jobAssigner")
public class JobAssigner {

    @Id
    @Column(name = "assignID", nullable = false, unique = true)
    private String assignID;

    @ManyToOne
    @JoinColumn(name = "careGiverID", nullable = true)
    private CareGiver careGiver;

    @ManyToOne
    @JoinColumn(name = "taskID", nullable = true)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "appointmentID", nullable = true)
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "assignerID",nullable = false)
    private Employee employee;

    @Column(name = "assignType", nullable = false)
    private String assignType;

    @Column(name = "assignTime")
    private Timestamp assignTime;

    @Column(name = "acceptanceType", nullable = false)
    private String acceptanceType;

    @PrePersist
    protected void onCreate() {
        if (assignTime == null) {
            assignTime = Timestamp.from(Instant.now());
        }
    }
}
