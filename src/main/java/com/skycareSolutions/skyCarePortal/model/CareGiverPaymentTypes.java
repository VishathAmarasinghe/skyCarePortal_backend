package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "caregiverPaymentTypes")
public class CareGiverPaymentTypes {
    @Id
    @Column(name = "paymentTypeID", nullable = false, unique = true)
    private String paymentTypeID  ;

    @Column(name = "name", nullable = false)
    private String paymentName;

    @Column(name = "state",nullable = false)
    private String state;
}
