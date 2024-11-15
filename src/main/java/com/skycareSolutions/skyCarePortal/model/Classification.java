package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "classification")
public class Classification {
    @Id
    @Column(name = "classificationID")
    private String classificationID;
    @Column(name = "classificationName")
    private String classificationName;
    @Column(name = "state")
    private String state;

    @ManyToMany(mappedBy = "classification")
    private List<ClientClassification> clientClassifications;

    public Classification(String classificationID) {
        this.classificationID = classificationID;
    }

    public Classification(){

    }
}
