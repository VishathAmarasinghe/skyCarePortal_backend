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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
        Set<String> consumedFiles = new HashSet<>();
        boolean allowUnmatchedSingleFile = files != null
                && files.size() == 1
                && documentList.size() == 1;
        for (CareGiverDocumentsDTO doc : documentList) {
            MultipartFile selectedFile = resolveFileForDocument(
                    doc, files, consumedFiles, allowUnmatchedSingleFile
            );
            if (selectedFile == null) {
                continue;
            }
            try {
                String storedPath = CareGiverDocumentFileUtil.storeFile(
                        doc.getCareGiverID(),
                        doc.getDocumentTypeID(),
                        selectedFile,
                        doc.getDocument()
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

        boolean singleFileUpload = files != null && files.size() == 1;
        List<CareGiverDocumentsDTO> existingDocuments = getDocumentsByCareGiverId(careGiverId);

        if (singleFileUpload) {
            inputDtos = narrowInputForSingleFileUpload(inputDtos, files, existingDocuments);
            if (inputDtos.isEmpty()) {
                return;
            }
        }

        boolean singleTargetedUpload = singleFileUpload && inputDtos.size() == 1;
        Set<String> consumedFiles = new HashSet<>();

        for (CareGiverDocumentsDTO input : inputDtos) {
            if (input == null || input.getDocumentTypeID() == null) {
                continue;
            }
            input.setCareGiverID(careGiverId);

            boolean alreadyExists = existingDocuments.stream()
                    .anyMatch(row -> row.getDocumentTypeID().equalsIgnoreCase(input.getDocumentTypeID()));
            if (!alreadyExists) {
                MultipartFile newFile = resolveFileForDocument(
                        input, files, consumedFiles, singleTargetedUpload
                );
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

            if (inputDoc.getDocument() == null || inputDoc.getDocument().isBlank()) {
                continue;
            }

            MultipartFile newFile = resolveFileForDocument(
                    inputDoc, files, consumedFiles, singleTargetedUpload
            );

            if (newFile != null) {
                CareGiverDocumentFileUtil.deleteFileIfExists(existingDoc.getDocument());
                try {
                    String storedPath = CareGiverDocumentFileUtil.storeFile(
                            careGiverId,
                            inputDoc.getDocumentTypeID(),
                            newFile,
                            inputDoc.getDocument()
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

            // Legacy mobile sends every document type with status "Pending"; skip bulk metadata writes
            // when a single new file is being uploaded.
            if (!singleFileUpload
                    && (expDateChanged || statusChanged)
                    && CareGiverDocumentFileUtil.sameDocumentReference(
                    inputDoc.getDocument(),
                    existingDoc.getDocument()
            )) {
                inputDoc.setDocument(existingDoc.getDocument());
                updateDocument(careGiverId, inputDoc.getDocumentTypeID(), inputDoc);
            }
        }
    }

    /**
     * Production mobile sends all active document types in one payload but only one multipart file.
     * Keep only the row that references the uploaded filename (non-persisted path).
     */
    private List<CareGiverDocumentsDTO> narrowInputForSingleFileUpload(
            List<CareGiverDocumentsDTO> inputDtos,
            List<MultipartFile> files,
            List<CareGiverDocumentsDTO> existingDocuments
    ) {
        if (files == null || files.isEmpty() || inputDtos == null || inputDtos.size() <= 1) {
            return inputDtos;
        }

        MultipartFile uploaded = files.get(0);
        String uploadedName = CareGiverDocumentFileUtil.effectiveOriginalFilename(uploaded, null);

        if (uploadedName != null && !uploadedName.isBlank()) {
            List<CareGiverDocumentsDTO> explicitUploadRows = inputDtos.stream()
                    .filter(dto -> dto != null
                            && dto.getDocument() != null
                            && !dto.getDocument().isBlank()
                            && !CareGiverDocumentFileUtil.isPersistedPath(dto.getDocument())
                            && CareGiverDocumentFileUtil.filenamesMatch(dto.getDocument(), uploadedName))
                    .collect(Collectors.toList());

            if (explicitUploadRows.size() == 1) {
                return explicitUploadRows;
            }
        }

        List<CareGiverDocumentsDTO> changedRows = inputDtos.stream()
                .filter(dto -> isChangedUploadRow(dto, existingDocuments))
                .collect(Collectors.toList());

        if (changedRows.size() == 1) {
            return changedRows;
        }

        if (changedRows.size() > 1 && uploadedName != null && !uploadedName.isBlank()) {
            List<CareGiverDocumentsDTO> changedMatching = changedRows.stream()
                    .filter(dto -> CareGiverDocumentFileUtil.filenamesMatch(dto.getDocument(), uploadedName))
                    .collect(Collectors.toList());
            if (changedMatching.size() == 1) {
                return changedMatching;
            }
        }

        List<CareGiverDocumentsDTO> withIntent = inputDtos.stream()
                .filter(dto -> dto != null
                        && CareGiverDocumentFileUtil.hasUploadIntent(dto.getDocument(), files))
                .collect(Collectors.toList());

        if (withIntent.size() == 1) {
            return withIntent;
        }

        if (withIntent.size() > 1) {
            List<CareGiverDocumentsDTO> nonPersisted = withIntent.stream()
                    .filter(dto -> !CareGiverDocumentFileUtil.isPersistedPath(dto.getDocument()))
                    .collect(Collectors.toList());
            if (nonPersisted.size() == 1) {
                return nonPersisted;
            }
        }

        return inputDtos;
    }

    private boolean isChangedUploadRow(
            CareGiverDocumentsDTO dto,
            List<CareGiverDocumentsDTO> existingDocuments
    ) {
        if (dto == null || dto.getDocumentTypeID() == null) {
            return false;
        }
        String inputDoc = dto.getDocument();
        if (inputDoc == null || inputDoc.isBlank()) {
            return false;
        }
        if (CareGiverDocumentFileUtil.isPersistedPath(inputDoc)) {
            return false;
        }

        Optional<CareGiverDocumentsDTO> existing = existingDocuments.stream()
                .filter(row -> row.getDocumentTypeID().equalsIgnoreCase(dto.getDocumentTypeID()))
                .findFirst();

        if (existing.isEmpty()) {
            return true;
        }

        return !CareGiverDocumentFileUtil.sameDocumentReference(
                inputDoc,
                existing.get().getDocument()
        );
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

    private MultipartFile resolveFileForDocument(
            CareGiverDocumentsDTO doc,
            List<MultipartFile> files,
            Set<String> consumedFiles,
            boolean acceptUnmatchedSingleFile
    ) {
        if (files == null || files.isEmpty() || doc == null) {
            return null;
        }
        String reference = doc.getDocument();

        MultipartFile matched = null;
        if (reference != null
                && !reference.isBlank()
                && CareGiverDocumentFileUtil.hasUploadIntent(reference, files)) {
            matched = CareGiverDocumentFileUtil.resolveMultipartFile(reference, files);
        }

        if (matched == null && acceptUnmatchedSingleFile && files.size() == 1) {
            matched = files.get(0);
        }

        if (matched == null) {
            return null;
        }

        String fileKey = CareGiverDocumentFileUtil
                .normalizeForMatch(
                        CareGiverDocumentFileUtil.effectiveOriginalFilename(matched, reference)
                )
                .toLowerCase();
        if (consumedFiles.contains(fileKey)) {
            return null;
        }
        consumedFiles.add(fileKey);
        return matched;
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
