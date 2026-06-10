package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agreement_template")
public class AgreementTemplate {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "template_key", nullable = false, unique = true)
    private String templateKey;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "audience", nullable = false)
    private String audience;

    @Column(name = "care_giver_type")
    private String careGiverType;

    @Column(name = "is_default_for_audience")
    private boolean defaultForAudience;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AgreementTemplateVersion> versions = new ArrayList<>();
}
