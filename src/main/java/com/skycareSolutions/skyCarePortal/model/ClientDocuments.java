package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "client_documents")
public class ClientDocuments {

    @Id
    @Column(name = "clientDocumentID", length = 20)
    private String clientDocumentID;
    @Column(name = "document")
    private String documentName;
    @Column(name = "docLocation")
    private String documentLocation;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
    @ManyToOne
    private Client clientid;
    @Column(name = "shareType")
    private String shareType;
}
