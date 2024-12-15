package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.ResourceDocumentDTO;
import com.skycareSolutions.skyCarePortal.model.Resource;
import com.skycareSolutions.skyCarePortal.model.ResourceDocument;
import com.skycareSolutions.skyCarePortal.repository.ResourceDocumentRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResourceDocumentService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "resources").toString();


    @Autowired
    private final ResourceDocumentRepository documentRepository;

    @Autowired
    private ResourceService resourceService;

    public ResourceDocumentService(ResourceDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    private ResourceDocument toEntity(ResourceDocumentDTO dto) {
        ResourceDocument resourceDocument = new ResourceDocument();
        resourceDocument.setDocumentId(dto.getDocumentId());
        resourceDocument.setResource(resourceService.getResourceByIdWithEntity(dto.getResourceID()));
        resourceDocument.setDocumentLocation(dto.getDocumentLocation());
        resourceDocument.setDocumentName(dto.getDocumentName());
        return  resourceDocument;
    }

    private ResourceDocumentDTO toDTO(ResourceDocument document) {
        ResourceDocumentDTO resourceDocumentDTO= new ResourceDocumentDTO();
        resourceDocumentDTO.setResourceID(document.getResource().getResourceId());
        resourceDocumentDTO.setDocumentLocation(document.getDocumentLocation());
        resourceDocumentDTO.setDocumentName(document.getDocumentName());
        resourceDocumentDTO.setDocumentId(document.getDocumentId());
        return  resourceDocumentDTO;
    }

    public ResourceDocumentDTO saveDocument(ResourceDocumentDTO dto) {
        String lastResourceDocID = documentRepository.getLastIdOfResourceDocument();
        String newResourceDocID = Micro_utils.idGenerator("resourceDocuments",lastResourceDocID);
        dto.setDocumentId(newResourceDocID);
        ResourceDocument document = toEntity(dto);
        ResourceDocument savedDocument = documentRepository.save(document);
        return toDTO(savedDocument);
    }

    public ResponseEntity<ResourceDocumentDTO> getDocumentById(String documentId) {
        ResourceDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return ResponseEntity.ok(toDTO(document));
    }

    public ResponseEntity<List<ResourceDocumentDTO>> getAllDocuments() {
        List<ResourceDocumentDTO> documents = documentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(documents);
    }

    public List<ResourceDocumentDTO> getDocumentsRelatedToResourceID(String resourceID){
        Resource resource = resourceService.getResourceByIdWithEntity(resourceID);
        List<ResourceDocument> resourceDocuments = documentRepository.getAllResourceDocumentsByResource(resource);
        List<ResourceDocumentDTO> resourceDocumentDTOList = new ArrayList<>();
        for (ResourceDocument resourceDocument:resourceDocuments){
            resourceDocumentDTOList.add(toDTO(resourceDocument));
        }
        return resourceDocumentDTOList;
    }

    public ResponseEntity<Void> deleteDocument(String documentId) {
        Optional<ResourceDocument> document = documentRepository.findById(documentId);
        documentRepository.deleteById(documentId);
        if (document.isPresent()){
            ResourceDocument doc = document.get();
            try {
                String fileName = doc.getDocumentLocation().substring(doc.getDocumentLocation().lastIndexOf("\\"));

                File destinationFile = new File(DIRECTORY, fileName);
                if (destinationFile.exists()) {
                    // Delete the existing file
                    boolean deleted = destinationFile.delete();
                    if (!deleted) {
                        throw new RuntimeException("Failed to delete the existing file: " + destinationFile.getName());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> deleteAllDocumentsRelatedToResource(String resourceID) {
        Resource resource = resourceService.getResourceByIdWithEntity(resourceID);

        // Get all documents related to the resource
        List<ResourceDocument> resourceDocuments = resource.getResourceDocuments();
        for (ResourceDocument resourceDocument:resourceDocuments){
            documentRepository.deleteById(resourceDocument.getDocumentId());
                try {
                    String fileName = resourceDocument.getDocumentLocation().substring(resourceDocument.getDocumentLocation().lastIndexOf("\\"));
                    File destinationFile = new File(DIRECTORY, fileName);
                    if (destinationFile.exists()) {
                        // Delete the existing file
                        boolean deleted = destinationFile.delete();
                        if (!deleted) {
                            throw new RuntimeException("Failed to delete the existing file: " + destinationFile.getName());
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }
        return ResponseEntity.noContent().build();
    }
}
