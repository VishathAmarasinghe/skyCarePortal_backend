package com.skycareSolutions.skyCarePortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskAttachmentsDTO {
    private String taskID;
    private String documentID;
    private String document;
}
