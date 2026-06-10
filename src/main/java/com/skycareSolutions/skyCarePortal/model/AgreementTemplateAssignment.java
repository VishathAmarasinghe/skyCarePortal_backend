package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agreement_template_assignment")
public class AgreementTemplateAssignment {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private AgreementTemplate template;

    @Column(name = "rule_type", nullable = false)
    private String ruleType;

    @Column(name = "care_giver_type")
    private String careGiverType;

    @Column(name = "priority")
    private int priority;
}
