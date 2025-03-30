package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private long chatID;
    private String senderID;
    private String content;
    private Long messageID;
    private LocalDateTime TimeStamp;
    private String senderName;
    private String senderProfilePic;
}
