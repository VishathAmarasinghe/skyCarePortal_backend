package com.skycareSolutions.skyCarePortal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatMessageDTO {

    private long chatID;
    private String senderID;
    private String content;
}
