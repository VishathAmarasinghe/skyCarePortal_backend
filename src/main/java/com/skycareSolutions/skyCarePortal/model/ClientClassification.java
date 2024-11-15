package com.skycareSolutions.skyCarePortal.model;

import com.skycareSolutions.skyCarePortal.model.Client;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "client_classification")
public class ClientClassification {

    public ClientClassification(){

    }

    @EmbeddedId
    private ClientClassificationID id;

    @ManyToOne
    @MapsId("clientID")
    @JoinColumn(name = "clientID")
    private Client client;

    @ManyToOne
    @MapsId("classificationID")
    @JoinColumn(name = "classificationID")
    private Classification classification;

    public ClientClassification(ClientClassificationID id, Client client, Classification classification) {
        this.id = id;
        this.client = client;
        this.classification = classification;
    }

}
