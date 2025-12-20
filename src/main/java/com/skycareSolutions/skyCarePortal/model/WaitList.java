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
@Table(name = "waitlist")
public class WaitList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name = "joined_at", updatable = false)
    private Timestamp joinedAt = new Timestamp(System.currentTimeMillis());

    public WaitList(String email) {
        this.email = email;
        this.joinedAt = new Timestamp(System.currentTimeMillis());
    }
}
