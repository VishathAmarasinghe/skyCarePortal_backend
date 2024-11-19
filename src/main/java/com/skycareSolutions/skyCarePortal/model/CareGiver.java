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

    @Column(name = "weekdaysDaytimeAmount")
    private double weekdaysDaytimeAmount;  // Amount for weekdays daytime shifts

    @Column(name = "weekDaysEveningAmount")
    private double weekDaysEveningAmount;  // Amount for weekdays evening shifts

    @Column(name = "weekDaysOvernightAmount")
    private double weekDaysOvernightAmount;  // Amount for weekdays overnight shifts

    @Column(name = "weekendsDaytimeAmount")
    private double weekendsDaytimeAmount;  // Amount for weekends daytime shifts

    @Column(name = "weekendsEveningAmount")
    private double weekendsEveningAmount;  // Amount for weekends evening shifts

    @Column(name = "weekendsOvernightAmount")
    private double weekendsOvernightAmount;  // Amount for weekends overnight shifts

    @Column(name = "additionalAmounts")
    private double additionalAmounts;  // Any additional amounts (e.g., bonuses, allowances)
}
