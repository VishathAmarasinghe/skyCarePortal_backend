package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResourceDTO {
    private String resourceId;
    private String resourceName;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String notes;
    private String shareType;
    private String creatorId;
    private List<ResourceDocumentDTO> resourceDocuments;
}
