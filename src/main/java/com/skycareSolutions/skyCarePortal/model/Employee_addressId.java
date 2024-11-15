package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class  Employee_addressId implements Serializable {

    public Employee_addressId() {
    }

    public Employee_addressId(String employeeID, String longitude, String latitude) {
        this.employeeID = employeeID;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Column(name = "employeeID", length = 20)
    private String employeeID;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "latitude")
    private String latitude;

    // Override equals and hashCode for proper comparison of composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee_addressId that = (Employee_addressId) o;
        return Objects.equals(employeeID, that.employeeID) &&
                Objects.equals(longitude, that.longitude) && Objects.equals(latitude,that.latitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeID, latitude,longitude);
    }
}
