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
@Table(name = "caregiver")
public class CareGiver {

    @Id
    @Column(name = "careGiverID", nullable = false, unique = true)
    private String careGiverID;  // Primary Key (not auto-generated)

    @ManyToOne
    @JoinColumn(name = "employeeID", nullable = false)
    private Employee employee;  // Foreign key to Employee table

    @Column(name = "status")
    private String status;  // Status of the caregiver (e.g., Active, Inactive)


}
