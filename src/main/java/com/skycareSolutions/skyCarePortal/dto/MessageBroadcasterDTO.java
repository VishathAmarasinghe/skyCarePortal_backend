package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.List;

@Data
public class MessageBroadcasterDTO {
    MessageDTO messageData;
    List<String> broadcastList;
}
