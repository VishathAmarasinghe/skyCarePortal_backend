package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "compliance_lookup")
public class ComplianceLookupItem {

    @Id
    @Column(name = "lookupID")
    private String lookupID;

    @Enumerated(EnumType.STRING)
    @Column(name = "lookupType", nullable = false)
    private ComplianceLookupType lookupType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "sortOrder")
    private Integer sortOrder;
}
