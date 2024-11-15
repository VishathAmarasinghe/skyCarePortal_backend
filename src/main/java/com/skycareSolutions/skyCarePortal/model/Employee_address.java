package com.skycareSolutions.skyCarePortal.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "employee_address")
public class Employee_address {


    @EmbeddedId
    private Employee_addressId id;

    public Employee_address(Employee_addressId id, Employee employee, String city, String address, String state, String country, String postal_code) {
        this.id = id;
        this.employee = employee;
        this.city = city;
        this.address = address;
        this.state = state;
        this.country = country;
        this.postal_code = postal_code;
    }

    @ManyToOne
    @JoinColumn(name = "employeeID", insertable = false, updatable = false) // Ensure proper mapping
    @JsonBackReference  // Prevent serialization of the back reference (Employee)
    private Employee employee;

    @Column(name = "address")
    private String address;
    @Column(name = "city")
    private String city;
    @Column(name = "state")
    private String state;
    @Column(name = "country")
    private String country;
    @Column(name = "postal_code")
    private String postal_code;


}
