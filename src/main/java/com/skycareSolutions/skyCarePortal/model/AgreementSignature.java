package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "agreement_signature")
public class AgreementSignature {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "owner_type", nullable = false)
    private String ownerType;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "signature_type", nullable = false)
    private String signatureType;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "saved_for_reuse")
    private boolean savedForReuse;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
