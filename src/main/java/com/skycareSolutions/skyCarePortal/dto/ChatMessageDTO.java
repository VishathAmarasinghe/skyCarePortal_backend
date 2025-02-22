package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private long chatID;
    private String senderID;
    private String content;
}
