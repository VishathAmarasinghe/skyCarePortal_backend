package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.List;


@Data
public class GroupChatRequestDTO {
    private String groupName;
    private List<String> userIDs;
}
