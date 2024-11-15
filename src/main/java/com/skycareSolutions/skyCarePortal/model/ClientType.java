package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "client_type")
public class ClientType {

    @Id
    @Column(name = "clientTypeID")  // Added column annotation
    private String clientTypeID;

    @Column(name = "name")  // Added column annotation
    private String name;

    @Column(name = "status")  // Added column annotation
    private String status;

    @OneToMany(mappedBy = "clientType")
    private List<Client> clients;

}
