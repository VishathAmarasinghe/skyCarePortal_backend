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
@Table(name = "shiftNotes")
public class ShiftNotes {
    @Id
    @Column(name = "noteID", nullable = false, unique = true)
    private String noteID;

    @Column(name = "title", nullable = true)
    private String title;

    @Column(name = "shift_Start_date", nullable = true)
    private LocalDate shiftStartDate;

    @Column(name = "shift_start_time",nullable = true)
    private String shiftStartTime;

    @Column(name = "shift_End_date", nullable = true)
    private LocalDate shiftEndDate;

    @Column(name = "shift_end_time", nullable = true)
    private String shiftEndTime;

    @Column(name = "System_shift_start_date", nullable = true)
    private LocalDate systemShiftStartDate;

    @Column(name = "System_shift_End_date", nullable = true)
    private LocalDate systemShiftEndDate;

    @Column(name = "System_shift_start_time",nullable = true)
    private String SystemShiftStartTime;

    @Column(name = "System_shift_end_time", nullable = true)
    private String SystemShiftEndTime;

    @ManyToOne
    @JoinColumn(name = "recurrentAppointmentID",nullable = true)
    private RecurrentAppointment recurrentAppointment;

    @ManyToOne
    @JoinColumn(name = "clientID",nullable = true)
    private Client client;

    @Column(name = "notes", columnDefinition = "TEXT",nullable = true)
    private String notes;

    @Column(name = "comments", columnDefinition = "TEXT",nullable = true)
    private String comments;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Employee employee;

    @Column(name = "State")
    private String state;

    @Column(name = "workHours", nullable = false, columnDefinition = "DOUBLE")
    private double workHours = 0.0;

    @ManyToOne
    @JoinColumn(name = "careGiverId")
    private CareGiver careGiver;
}
