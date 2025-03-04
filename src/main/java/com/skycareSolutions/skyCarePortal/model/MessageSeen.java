package com.skycareSolutions.skyCarePortal.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messageSeen")
public class MessageSeen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @Column(name = "userId")
    private String userId;

    @Column(name = "readStatus")
    private boolean readStatus;

    @Column(name = "seenAt",nullable = true )
    private LocalDateTime seenAt;
}
