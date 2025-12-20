package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "naplicWaitlist")
public class NaplicWaitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "signup_method", nullable = false)
    private String signupMethod; // "GOOGLE" or "EMAIL"

    @Column(name = "joined_at", updatable = false)
    private Timestamp joinedAt = new Timestamp(System.currentTimeMillis());

    public NaplicWaitlist(String email, String name, String signupMethod) {
        this.email = email;
        this.name = name;
        this.signupMethod = signupMethod;
        this.joinedAt = new Timestamp(System.currentTimeMillis());
    }

    public NaplicWaitlist(String email) {
        this.email = email;
        this.signupMethod = "EMAIL";
        this.joinedAt = new Timestamp(System.currentTimeMillis());
    }
}

