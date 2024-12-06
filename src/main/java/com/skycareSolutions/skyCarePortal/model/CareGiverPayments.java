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
@Table(name = "caregiverPayments")
public class CareGiverPayments {

    @EmbeddedId
    private CareGiverPaymentIDs id;

    @ManyToOne
    @JoinColumn(name = "careGiverID", insertable = false, updatable = false)
    private CareGiver careGiver;

    @ManyToOne
    @JoinColumn(name = "paymentTypeID", insertable = false, updatable = false)
    private CareGiverPaymentTypes paymentType;

    @Column(name = "amount")
    private double amount;
}
