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
@Table(name = "signature")
public class Signature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "employeeID", length = 20, nullable = false)
    private String employeeID;

    @Column(name = "signature", nullable = false)
    private String signature; // File path to the signature image

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "employeeID", referencedColumnName = "employeeID", insertable = false, updatable = false)
    private Employee employee;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
