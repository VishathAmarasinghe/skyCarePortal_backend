package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EmployeeJobRolesId implements Serializable {

    @Column(name = "employeeID",length = 20)
    private String employeeID;

    @Column(name = "jobRole")
    private String jobRole;

    // Default constructor
    public EmployeeJobRolesId() {}

    // Constructor
    public EmployeeJobRolesId(String employeeID, String jobRole) {
        this.employeeID = employeeID;
        this.jobRole = jobRole;
    }

    // Getters and Setters
    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    // Override equals and hashCode for proper comparison of composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeJobRolesId that = (EmployeeJobRolesId) o;
        return Objects.equals(employeeID, that.employeeID) &&
                Objects.equals(jobRole, that.jobRole);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeID, jobRole);
    }
}
