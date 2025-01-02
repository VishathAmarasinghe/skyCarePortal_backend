package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.NoteDocumentsDTO;
import com.skycareSolutions.skyCarePortal.dto.ResourceDTO;
import com.skycareSolutions.skyCarePortal.dto.ResourceDocumentDTO;
import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.model.Resource;
import com.skycareSolutions.skyCarePortal.model.ResourceDocument;
import com.skycareSolutions.skyCarePortal.repository.ResourceDocumentRepository;
import com.skycareSolutions.skyCarePortal.repository.ResourceRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "resources").toString();


    @Autowired
    private final ResourceRepository resourceRepository;

    @Autowired
    @Lazy
    private ResourceDocumentService resourceDocumentService;

    @Autowired
    private EmployeeService employeeService;

    public ResourceService(ResourceRepository resourceRepository, ResourceDocumentRepository documentRepository) {
        this.resourceRepository = resourceRepository;
    }

    // Convert DTO to Entity
    private Resource toEntity(ResourceDTO dto) {
        Resource resource = new Resource();
        resource.setResourceName(dto.getResourceName());
        resource.setResourceId(dto.getResourceId());
        resource.setValidFrom(dto.getValidFrom());
        resource.setValidTo(dto.getValidTo());
        resource.setNotes(dto.getNotes());
        resource.setShareType(dto.getShareType());
        resource.setCreator(employeeService.findOneEmployeeWithEntity(dto.getCreatorId()));
        return resource;
    }

    // Convert Entity to DTO
    private ResourceDTO toDTO(Resource resource) {
        ResourceDTO dto = new ResourceDTO();
        dto.setResourceId(resource.getResourceId());
        dto.setResourceName(resource.getResourceName());
        dto.setValidFrom(resource.getValidFrom());
        dto.setValidTo(resource.getValidTo());
        dto.setNotes(resource.getNotes());
        dto.setShareType(resource.getShareType());
        dto.setCreatorId(resource.getCreator() != null ? resource.getCreator().getEmployeeID() : null);
        List<ResourceDocumentDTO> resourceDocumentDTOS = resourceDocumentService.getDocumentsRelatedToResourceID(resource.getResourceId());
        dto.setResourceDocuments(resourceDocumentDTOS);
        return dto;
    }

    private ResourceDocumentDTO toDocumentDTO(ResourceDocument document) {
        ResourceDocumentDTO dto = new ResourceDocumentDTO();
        dto.setDocumentId(document.getDocumentId());
        dto.setDocumentName(document.getDocumentName());
        dto.setDocumentLocation(document.getDocumentLocation());
        return dto;
    }

    private ResourceDocument toDocumentEntity(MultipartFile file, Resource resource) throws IOException {
        ResourceDocument document = new ResourceDocument();
        document.setDocumentName(file.getOriginalFilename());
        document.setDocumentLocation("/files/" + file.getOriginalFilename()); // Adjust path as per storage strategy
        document.setResource(resource);
        return document;
    }

    public ResourceDTO saveResourceWithFiles(ResourceDTO dto,List<MultipartFile> files) {
        try {
            String lastResourceID = resourceRepository.getLastIdOfResource();
            String newResourceID = Micro_utils.idGenerator("resource",lastResourceID);
            dto.setResourceId(newResourceID);
            Resource resource = toEntity(dto);
            Resource savedResource = resourceRepository.save(resource);

            try {
                if (files!=null && !files.isEmpty()){
                    for (MultipartFile file:files){
                        if (file!=null){
                            String originalFileName = file.getOriginalFilename();
                            String fileExtension = originalFileName;
                            if (originalFileName != null && originalFileName.contains(".")) {
                                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                            }
                            System.out.println("original file name "+originalFileName);
                            File directory = new File(DIRECTORY);
                            if(!directory.exists()){
                                directory.mkdirs();
                            }
                            File destinationFile = new File(DIRECTORY, newResourceID+ fileExtension);
                            if (destinationFile.exists()) {
                                // Delete the existing file
                                boolean deleted = destinationFile.delete();
                                if (!deleted) {
                                    throw new RuntimeException("Failed to delete the existing file: " + destinationFile.getName());
                                }
                            }

                            file.transferTo(destinationFile);
                            ResourceDocumentDTO docs  = new ResourceDocumentDTO();
                            docs.setDocumentLocation(destinationFile.getAbsolutePath());
                            docs.setDocumentName(originalFileName);
                            docs.setResourceID(resource.getResourceId());
                            resourceDocumentService.saveDocument(docs);
                        }
                    }
                }
                return toDTO(savedResource);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save resource and files", e);
        }
    }

    public ResourceDTO updateResourceWithFiles(String resourceId,ResourceDTO dto,List<MultipartFile> files) {
        try {
            Resource exisitingResource = getResourceByIdWithEntity(resourceId);
            exisitingResource.setCreator(employeeService.findOneEmployeeWithEntity(dto.getCreatorId()));
            exisitingResource.setNotes(dto.getNotes());
            exisitingResource.setResourceName(dto.getResourceName());
            exisitingResource.setShareType(dto.getShareType());
            exisitingResource.setValidTo(dto.getValidTo());
            exisitingResource.setValidFrom(dto.getValidFrom());

            List<String> dtoDocumentIds = dto.getResourceDocuments().stream()
                    .map(ResourceDocumentDTO::getDocumentId)
                    .collect(Collectors.toList());

            // Find and remove documents that are not in the DTO's document list
            List<ResourceDocument> documentsToRemove = exisitingResource.getResourceDocuments().stream()
                    .filter(doc -> !dtoDocumentIds.contains(doc.getDocumentId()))
                    .collect(Collectors.toList());

            for (ResourceDocument docs:documentsToRemove){
                resourceDocumentService.deleteDocument(docs.getDocumentId());
            }
            Resource savedResource = resourceRepository.save(exisitingResource);

            try {
                if (files!=null && !files.isEmpty()){
                    for (MultipartFile file:files){
                        if (file!=null){
                            String originalFileName = file.getOriginalFilename();
                            String fileExtension = originalFileName;
                            if (originalFileName != null && originalFileName.contains(".")) {
                                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                            }
                            System.out.println("original file name "+originalFileName);
                            File directory = new File(DIRECTORY);
                            if(!directory.exists()){
                                directory.mkdirs();
                            }
                            File destinationFile = new File(DIRECTORY, exisitingResource.getResourceId()+"_"+ fileExtension);
                            if (destinationFile.exists()) {
                                // Delete the existing file
                                boolean deleted = destinationFile.delete();
                                if (!deleted) {
                                    throw new RuntimeException("Failed to delete the existing file: " + destinationFile.getName());
                                }
                            }

                            file.transferTo(destinationFile);
                            ResourceDocumentDTO docs  = new ResourceDocumentDTO();
                            docs.setDocumentLocation(destinationFile.getAbsolutePath());
                            docs.setDocumentName(originalFileName);
                            docs.setResourceID(exisitingResource.getResourceId());
                            resourceDocumentService.saveDocument(docs);
                        }
                    }
                }
                return toDTO(savedResource);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save resource and files", e);
        }
    }

    public Resource getResourceByIdWithEntity(String resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
    }

    public ResourceDTO getResourceById(String resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        return toDTO(resource);
    }

    public List<ResourceDTO> getAllResources() {
        return resourceRepository.findAll().stream().map(this::toDTO).toList();
    }

    public void deleteResource(String resourceId) {
        resourceDocumentService.deleteAllDocumentsRelatedToResource(resourceId);
        resourceRepository.deleteById(resourceId);
    }
}
