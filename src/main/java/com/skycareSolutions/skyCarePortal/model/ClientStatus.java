package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "client_status")
public class ClientStatus {

    @Id
    @Column(name = "clientStatusID")  // Added column annotation
    private String clientStatusID;

    @Column(name = "status")  // Added column annotation
    private String status;

    @OneToMany(mappedBy = "clientStatus")
    private List<Client> clients;
}
