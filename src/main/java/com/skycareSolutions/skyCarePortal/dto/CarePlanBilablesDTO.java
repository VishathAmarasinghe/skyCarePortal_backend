package com.skycareSolutions.skyCarePortal.dto;

import com.skycareSolutions.skyCarePortal.model.CarePlan;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class CarePlanBilablesDTO {

    private String billID;
    private String name;
    private double amount;
    private String careplanID;
}
