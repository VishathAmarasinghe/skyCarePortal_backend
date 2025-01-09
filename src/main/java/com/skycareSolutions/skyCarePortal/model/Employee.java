package com.skycareSolutions.skyCarePortal.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @Column(name = "employeeID", length = 20)
    private String employeeID;
    @Column(name = "firstName")
    private String firstName;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "accessRole")
    private String accessRole;
    @Column(name = "profile_photo")
    private String profile_photo;
    @Column(name = "joinDate")
    private Date joinDate;
    @Column(name = "status")
    private String status;
    @OneToMany(mappedBy = "employee",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Employee_address> employeeAddresses;
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Employee_job_Roles> employeeJobRoles;
    @OneToMany(mappedBy = "employee",cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Employee_phoneNo> employeePhoneNo;
}
