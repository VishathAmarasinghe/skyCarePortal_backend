package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CareGiverDocumentsDTO;
import com.skycareSolutions.skyCarePortal.model.CareGiver;
import com.skycareSolutions.skyCarePortal.model.CareGiverDocuments;
import com.skycareSolutions.skyCarePortal.model.CareGiverDocumentsId;
import com.skycareSolutions.skyCarePortal.repository.CareGiverDocumentsRepository;
import com.skycareSolutions.skyCarePortal.utils.CareGiverDocumentFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CareGiverDocumentService {

    @Autowired
    private CareGiverDocumentsRepository careGiverDocumentsRepository;

    @Autowired
    @Lazy
    private CareGiverService careGiverService;

    public List<CareGiverDocumentsDTO> getAllDocuments() {
        return careGiverDocumentsRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CareGiverDocumentsDTO> getDocumentsByCareGiverId(String careGiverID) {
        CareGiver careGiver = careGiverService.getCareGiverByIdWithEntity(careGiverID);
        return careGiverDocumentsRepository.findByCareGiverID(careGiver)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void addDocument(List<CareGiverDocumentsDTO> documentList, List<MultipartFile> files) {
        if (documentList == null) {
            return;
        }
        for (CareGiverDocumentsDTO doc : documentList) {
            MultipartFile selectedFile = resolveFileForDocument(doc, files);
            if (selectedFile == null) {
                continue;
            }
            try {
                String storedPath = CareGiverDocumentFileUtil.storeFile(
                        doc.getCareGiverID(),
                        doc.getDocumentTypeID(),
                        selectedFile
                );
                doc.setDocument(storedPath);
                careGiverDocumentsRepository.save(mapToEntity(doc));
            } catch (Exception e) {
                throw new RuntimeException("Error storing caregiver document", e);
            }
        }
    }

    public void processDocumentUpdates(
            String careGiverId,
            List<CareGiverDocumentsDTO> inputDtos,
            List<MultipartFile> files
    ) {
        if (inputDtos == null || inputDtos.isEmpty()) {
            return;
        }

        List<CareGiverDocumentsDTO> existingDocuments = getDocumentsByCareGiverId(careGiverId);

        for (CareGiverDocumentsDTO input : inputDtos) {
            if (input == null || input.getDocumentTypeID() == null) {
                continue;
            }
            input.setCareGiverID(careGiverId);

            boolean alreadyExists = existingDocuments.stream()
                    .anyMatch(row -> row.getDocumentTypeID().equalsIgnoreCase(input.getDocumentTypeID()));
            if (!alreadyExists) {
                MultipartFile newFile = resolveFileForDocument(input, files);
                if (newFile != null) {
                    addDocument(List.of(input), List.of(newFile));
                }
            }
        }

        for (CareGiverDocumentsDTO existingDoc : existingDocuments) {
            CareGiverDocumentsDTO inputDoc = inputDtos.stream()
                    .filter(row -> row.getDocumentTypeID() != null
                            && row.getDocumentTypeID().equalsIgnoreCase(existingDoc.getDocumentTypeID()))
                    .findFirst()
                    .orElse(null);

            if (inputDoc == null) {
                continue;
            }

            inputDoc.setCareGiverID(careGiverId);
            MultipartFile newFile = resolveFileForDocument(inputDoc, files);

            if (newFile != null) {
                CareGiverDocumentFileUtil.deleteFileIfExists(existingDoc.getDocument());
                try {
                    String storedPath = CareGiverDocumentFileUtil.storeFile(
                            careGiverId,
                            inputDoc.getDocumentTypeID(),
                            newFile
                    );
                    inputDoc.setDocument(storedPath);
                    updateDocument(careGiverId, inputDoc.getDocumentTypeID(), inputDoc);
                } catch (Exception e) {
                    throw new RuntimeException("Error uploading caregiver document", e);
                }
                continue;
            }

            boolean expDateChanged = !Objects.equals(inputDoc.getExpDate(), existingDoc.getExpDate());
            boolean statusChanged = inputDoc.getStatus() != null
                    && !inputDoc.getStatus().equalsIgnoreCase(existingDoc.getStatus());
            if ((expDateChanged || statusChanged) && hasDocumentReference(existingDoc.getDocument())) {
                inputDoc.setDocument(existingDoc.getDocument());
                updateDocument(careGiverId, inputDoc.getDocumentTypeID(), inputDoc);
            }
        }
    }

    public CareGiverDocumentsDTO updateDocument(String careGiverID, String documentTypeID, CareGiverDocumentsDTO dto) {
        CareGiverDocumentsId ids = new CareGiverDocumentsId(careGiverID, documentTypeID);
        if (careGiverDocumentsRepository.existsById(ids)) {
            CareGiverDocuments document = mapToEntity(dto);
            document.setId(ids);
            return mapToDTO(careGiverDocumentsRepository.save(document));
        } else {
            throw new RuntimeException("Document not found for the given IDs");
        }
    }

    public void deleteDocument(String careGiverID, String documentTypeID) {
        CareGiverDocumentsId ids = new CareGiverDocumentsId(careGiverID, documentTypeID);
        Optional<CareGiverDocuments> document = careGiverDocumentsRepository.findById(ids);
        if (document.isPresent()) {
            CareGiverDocumentFileUtil.deleteFileIfExists(document.get().getDocument());
            careGiverDocumentsRepository.deleteById(ids);
        }
    }

    private MultipartFile resolveFileForDocument(CareGiverDocumentsDTO doc, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return null;
        }
        String reference = doc.getDocument();
        return CareGiverDocumentFileUtil.resolveMultipartFile(reference, files);
    }

    private boolean hasDocumentReference(String document) {
        return document != null && !document.isBlank();
    }

    private CareGiverDocumentsDTO mapToDTO(CareGiverDocuments document) {
        CareGiverDocumentsDTO dto = new CareGiverDocumentsDTO();
        dto.setCareGiverID(document.getId().getCareGiverID());
        dto.setDocumentTypeID(document.getId().getDocumentTypeID());
        dto.setExpDate(document.getExpDate());
        dto.setStatus(document.getStatus());
        dto.setDocument(document.getDocument());
        return dto;
    }

    private CareGiverDocuments mapToEntity(CareGiverDocumentsDTO dto) {
        CareGiverDocuments document = new CareGiverDocuments();
        CareGiverDocumentsId ids = new CareGiverDocumentsId(dto.getCareGiverID(), dto.getDocumentTypeID());
        document.setId(ids);
        document.setExpDate(dto.getExpDate());
        document.setStatus(dto.getStatus());
        document.setDocument(dto.getDocument());
        return document;
    }
}
