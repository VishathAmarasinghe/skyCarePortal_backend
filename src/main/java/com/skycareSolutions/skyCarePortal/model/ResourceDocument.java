package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ResourceDocument {

    @Id
    private String documentId;

    private String documentName;

    private String documentLocation;

    @ManyToOne
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;
}
