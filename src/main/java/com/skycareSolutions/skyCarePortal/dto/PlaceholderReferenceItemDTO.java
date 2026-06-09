package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceholderReferenceItemDTO {
    private String token;
    private String category;
    private String description;
    private String exampleOutput;
    private String sourceEntity;
}
