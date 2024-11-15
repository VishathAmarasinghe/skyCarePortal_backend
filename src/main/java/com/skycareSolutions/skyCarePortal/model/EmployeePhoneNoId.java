package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EmployeePhoneNoId implements Serializable {

    @Column(name = "employeeID")
    private String employeeID;
    @Column(name = "phoneNo")
    private String phoneNo;

    // Default constructor
    public EmployeePhoneNoId() {}

    // Constructor
    public EmployeePhoneNoId(String employeeID, String phoneNo) {
        this.employeeID = employeeID;
        this.phoneNo = phoneNo;
    }

    // Getters and Setters
    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    // Override equals and hashCode for composite key comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePhoneNoId that = (EmployeePhoneNoId) o;
        return Objects.equals(employeeID, that.employeeID) &&
                Objects.equals(phoneNo, that.phoneNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeID, phoneNo);
    }
}
