package com.skycareSolutions.skyCarePortal.model;

import com.skycareSolutions.skyCarePortal.model.Chat;
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
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @JoinColumn(name = "sender_id")
    private String senderID;

    private String content;
    private LocalDateTime timestamp = LocalDateTime.now();
}
