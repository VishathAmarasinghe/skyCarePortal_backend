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
@Table(name = "incidents")
public class Incidents {

    @Id
    @Column(name = "incidentID")
    private String incidentID;

    @Column(name = "title")
    private String title;

    @Column(name = "reportDate")
    private LocalDate reportDate;

    @Column(name = "incidentDate")
    private LocalDate incidentDate;

    @Column(name = "incidentTime")
    private LocalDateTime incidentTime;

    @ManyToOne
    @JoinColumn(name = "incidentTypeID")
    private IncidentType incidentType;

    @ManyToOne
    @JoinColumn(name = "incidentStatusID")
    private IncidentStatus incidentStatus;

    @ManyToOne
    @JoinColumn(name = "addressID")
    private AppointmentAddress appointmentAddress;

    @Column(name = "issue")
    private String issue;

    @Column(name = "description")
    private String description;

    @Column(name = "followUp")
    private String followUp;

    @Column(name = "notes")
    private String notes;

    @Column(name = "hospitalizedStatus")
    private boolean hospitalized;

    @Column(name = "admitDate")
    private LocalDate admitDate;

    @Column(name = "dischargeDate")
    private LocalDate dischargeDate;

    @ManyToOne
    @JoinColumn(name = "carePlanID")
    private CarePlan carePlan;

    @ManyToOne
    @JoinColumn(name = "appointmentID")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "taskID")
    private Task task;

}
