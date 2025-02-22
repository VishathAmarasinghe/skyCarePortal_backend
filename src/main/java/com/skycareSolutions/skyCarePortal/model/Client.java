package com.skycareSolutions.skyCarePortal.model;

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
@Table(name = "client")
public class Client {

    @Id
    @Column(name = "clientID", length = 20)
    private String clientID;
    @Column(name = "firstName")
    private String firstName;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "preferredName")
    private String preferredName;
    @Column(name = "email")
    private String email;
    @Column(name = "gender")
    private String gender;
    @Column(name = "birthday")
    private Date birthday;
    @Column(name = "aboutMe")
    private String aboutMe;
    @Column(name = "joinDate")
    private String joinDate;
    @Column(name = "interests")
    private String interests;
    @Column(name = "dislikes")
    private String dislikes;
    @Column(name = "profilePhoto")
    private String profilePhoto;

    @Column(name = "referenceNo")
    private String referenceNo;

    @Column(name = "emergencyPhoneNo",nullable = true)
    private String emergencyPhoneNo;

    @Column(name = "emergencyUserName" ,nullable = true)
    private String emergencyUserName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "physicalAddressID")
    private ClientPhysicalAddress physicalAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "postalAddressID")
    private ClientPostalAddress postalAddress;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ClientLanguage> clientLanguages;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientPhoneNo> phoneNumbers;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientClassification> clientClassifications;

    @ManyToOne
    @JoinColumn(name = "clientTypeID")
    private ClientType clientType;

    @ManyToOne
    @JoinColumn(name = "clientStatusID")
    private ClientStatus clientStatus;

    @Column(name = "password")
    private String password;

    @Column(name = "status", columnDefinition = "varchar(255) default 'Deactivated'")
    private String status;
}
