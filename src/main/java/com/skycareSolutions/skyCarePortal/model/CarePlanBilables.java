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
@Table(name = "careplanBilables")
public class CarePlanBilables {

    @Id
    @Column(name = "billID")
    private String billID;

    @Column(name = "name")
    private String name;

    @Column(name = "amount")
    private double amount;

    @ManyToOne
    @JoinColumn(name = "careplanID")
    private CarePlan careplanID;

}
