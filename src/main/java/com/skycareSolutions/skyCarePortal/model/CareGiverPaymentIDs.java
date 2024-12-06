package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CareGiverPaymentIDs implements Serializable {

    @Column(name = "careGiverID", nullable = false)
    private String careGiverID;

    @Column(name = "paymentTypeID", nullable = false)
    private String paymentTypeID;
}
