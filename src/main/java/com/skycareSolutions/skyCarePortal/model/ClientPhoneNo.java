package com.skycareSolutions.skyCarePortal.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "client_phone_no")
public class ClientPhoneNo {

    @EmbeddedId
    private ClientPhoneNoId id;

    @ManyToOne
    @JoinColumn(name = "clientID", insertable = false, updatable = false) // Ensure proper mapping
    @JsonBackReference  // Prevent serialization of the back reference (Client)
    private Client client;

    // Constructors
    public ClientPhoneNo() {}

    public ClientPhoneNo(ClientPhoneNoId id, Client client) {
        this.id = id;
        this.client = client;
    }

    // Getters and Setters
    public ClientPhoneNoId getId() {
        return id;
    }

    public void setId(ClientPhoneNoId id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
