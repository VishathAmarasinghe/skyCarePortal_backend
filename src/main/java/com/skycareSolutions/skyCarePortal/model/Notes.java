package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.mapping.TableOwner;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notes")
public class Notes {

    @Id
    @Column(name = "noteID", nullable = false, unique = true)
    private String noteID;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "create_date", nullable = false)
    private String createDate;

    @Column(name = "shift_start_time",nullable = true)
    private String shiftStartTime;

    @Column(name = "shift_end_time", nullable = true)
    private String shiftEndTime;

    @ManyToOne
    @JoinColumn(name = "careplanID")
    private CarePlan careplanID;

    @ManyToOne
    @JoinColumn(name = "clientID")
    private Client clientID;

    @Column(name = "noteType")
    private String noteType;

    @ManyToOne
    @JoinColumn(name = "taskID")
    private Task taskID;

    @ManyToOne
    @JoinColumn(name = "appointmentID")
    private Appointment appointmentID;

    @Column(name = "effective_date",nullable = true)
    private String effectiveDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "shared_group")
    private String sharedGroup;

    @Column(name = "created_by")
    private String createdBy;
}
