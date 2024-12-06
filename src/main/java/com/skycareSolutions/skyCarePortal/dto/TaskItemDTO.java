package com.skycareSolutions.skyCarePortal.dto;

import com.skycareSolutions.skyCarePortal.model.Task;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class TaskItemDTO {

    private String itemID;
    private String taskID;
    private String title;
    private String description;
}
