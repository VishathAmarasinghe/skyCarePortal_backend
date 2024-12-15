package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Resource {

    @Id
    private String resourceId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    @Column
    private String notes;

    @Column
    private String shareType;

    @Column
    private String resourceName;

    @ManyToOne
    private Employee creator;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceDocument> resourceDocuments;
}
