package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "admin_saved_signature")
public class AdminSavedSignature {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "admin_employee_id", nullable = false, unique = true)
    private String adminEmployeeId;

    @Column(name = "signature_id", nullable = false)
    private String signatureId;

    @Column(name = "is_default")
    private boolean defaultSignature;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
