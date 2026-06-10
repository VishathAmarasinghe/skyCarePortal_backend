package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlaceholderReferenceCategoryDTO {
    private String category;
    private List<PlaceholderReferenceItemDTO> items;
}
