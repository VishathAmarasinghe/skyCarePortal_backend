package com.skycareSolutions.skyCarePortal.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "employeephoneno")
public class Employee_phoneNo {
    public Employee_phoneNo() {
    }

    @EmbeddedId
    private EmployeePhoneNoId id;

    @ManyToOne
    @JoinColumn(name = "employeeID", insertable = false, updatable = false) // Ensure proper mapping
    @JsonBackReference  // Prevent serialization of the back reference (Employee)
    private Employee employee;


    // Constructor that sets both id and employee
    public Employee_phoneNo(EmployeePhoneNoId id, Employee employee) {
        this.id = id;
        this.employee = employee;
    }

    // Getters and setters for id and employee (if needed)
    public EmployeePhoneNoId getId() {
        return id;
    }

    public void setId(EmployeePhoneNoId id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }


}
