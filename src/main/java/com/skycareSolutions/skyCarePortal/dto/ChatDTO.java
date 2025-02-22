package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class ChatDTO {
    private long chatID;
    private String chatName;
    private boolean isGroup;
    private String chatIcon;
}
