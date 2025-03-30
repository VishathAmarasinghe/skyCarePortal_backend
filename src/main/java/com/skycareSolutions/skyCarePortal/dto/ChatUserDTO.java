package com.skycareSolutions.skyCarePortal.dto;


import lombok.Data;

@Data
public class ChatUserDTO {
    private String userID;
    private String firstName;
    private String lastName;
    private String profilePhoto;
    private String userRole;
}
