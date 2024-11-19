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
@Table(name = "careplanStatus")
public class CarePlanStatus {

    @Id
    @Column(name = "careplanStatusID", nullable = false, unique = true)
    private String careplanStatusID;

    @Column(name = "status", nullable = false)
    private String status;
}
