package com.skycareSolutions.skyCarePortal.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "employeejobroles")
public class Employee_job_Roles {

    @EmbeddedId
    private EmployeeJobRolesId id;

    @ManyToOne
    @JoinColumn(name = "employeeID", insertable = false, updatable = false) // Ensure proper mapping
    @JsonBackReference  // Prevent serialization of the back reference (Employee)
    private Employee employee;

    // Optionally, you can add constructors, getters, setters, etc.
    public Employee_job_Roles() {}

    public Employee_job_Roles(EmployeeJobRolesId id,Employee employee){
        this.id=id;
        this.employee=employee;

    }

    public EmployeeJobRolesId getId() {
        return id;
    }

    public void setId(EmployeeJobRolesId id) {
        this.id = id;
    }
}
