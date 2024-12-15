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
@Table(name = "otp_records")
public class OtpRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email; // The email to which the OTP is sent

    @Column(nullable = false)
    private String otp; // The generated OTP

    @Column(nullable = false)
    private LocalDateTime expiryTime; // When the OTP will expire

    @Column(nullable = false)
    private boolean isUsed = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now();

}