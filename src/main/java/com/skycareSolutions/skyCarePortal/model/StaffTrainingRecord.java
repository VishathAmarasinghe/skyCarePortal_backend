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
@Table(name = "staff_training_record")
public class StaffTrainingRecord {

    @Id
    @Column(name = "recordID")
    private String recordID;

    @ManyToOne(optional = true)
    @JoinColumn(name = "employeeID", nullable = true)
    private Employee employee;

    @Column(name = "staffNameManual")
    private String staffNameManual;

    @Column(name = "position")
    private String position;

    @Column(name = "trainingCourse")
    private String trainingCourse;

    @Column(name = "provider")
    private String provider;

    @Column(name = "dateCompleted")
    private LocalDate dateCompleted;

    @Column(name = "expiryDate")
    private LocalDate expiryDate;

    @Column(name = "certificateFiled")
    private boolean certificateFiled;

    @Column(name = "certificatePath")
    private String certificatePath;

    @Column(name = "competencyAssessed")
    private boolean competencyAssessed;

    @ManyToOne(optional = true)
    @JoinColumn(name = "assessorEmployeeID", nullable = true)
    private Employee assessorEmployee;

    @Column(name = "assessorNameManual")
    private String assessorNameManual;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "createdBy")
    private String createdBy;
}
