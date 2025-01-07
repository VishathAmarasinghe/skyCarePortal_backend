package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skycareSolutions.skyCarePortal.dto.ResourceDTO;
import com.skycareSolutions.skyCarePortal.model.Resource;
import com.skycareSolutions.skyCarePortal.service.ResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService resourceService;

    private final ObjectMapper objectMapper;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Register the JSR310 module
    }

    @PostMapping
    public ResponseEntity<ResourceDTO> createResourceWithFiles(@RequestPart("resourceData") String resourceDTO,
                                                               @RequestPart("files") List<MultipartFile> files) {
        try {
            ResourceDTO resource = objectMapper.readValue(resourceDTO, ResourceDTO.class);
            ResourceDTO savedResource = resourceService.saveResourceWithFiles(resource,files);
            return ResponseEntity.ok(savedResource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResourceDTO> updateResourceWithFiles(
            @PathVariable("id") String resourceID,
            @RequestPart("resourceData") String resourceDTO,
                                                               @RequestPart(value = "files",required = false) List<MultipartFile> files) {
        try {
            ResourceDTO resource = objectMapper.readValue(resourceDTO, ResourceDTO.class);
            ResourceDTO savedResource = resourceService.updateResourceWithFiles(resourceID,resource,files);
            return ResponseEntity.ok(savedResource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceDTO> getResourceById(@PathVariable String id) {
        ResourceDTO resource = resourceService.getResourceById(id);
        return ResponseEntity.ok(resource);
    }

    @GetMapping
    public ResponseEntity<List<ResourceDTO>> getAllResources() {
        List<ResourceDTO> resources = resourceService.getAllResources();
        return ResponseEntity.ok(resources);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable String id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
