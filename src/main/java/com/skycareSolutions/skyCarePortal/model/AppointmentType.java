package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "appointmentType")
public class AppointmentType {

    @Id
    @Column(name = "appointmentTypeID", nullable = false, unique = true)
    private String appointmentTypeID;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "color", nullable = false)
    private String color;
}
