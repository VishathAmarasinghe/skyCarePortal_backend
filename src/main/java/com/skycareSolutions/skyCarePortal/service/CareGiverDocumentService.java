package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CareGiverDocumentsDTO;
import com.skycareSolutions.skyCarePortal.model.CareGiver;
import com.skycareSolutions.skyCarePortal.model.CareGiverDocuments;
import com.skycareSolutions.skyCarePortal.model.CareGiverDocumentsId;
import com.skycareSolutions.skyCarePortal.repository.CareGiverDocumentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CareGiverDocumentService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "careGiverDocuments").toString();


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
        for (CareGiverDocumentsDTO doc : documentList) {
            // Find the file that matches the document by its filename
            MultipartFile selectedFile = files.stream()
                    .filter(file -> file.getOriginalFilename() != null && file.getOriginalFilename().equalsIgnoreCase(doc.getDocument()))
                    .findFirst()
                    .orElse(null); // Return null if no match is found

            if (selectedFile != null) {
                try {

                    String originalFileName = selectedFile.getOriginalFilename();
                    String fileExtension = originalFileName;
                    if (originalFileName != null && originalFileName.contains(".")) {
                        fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                    }
                    System.out.println("original file name " + originalFileName);
                    File directory = new File(DIRECTORY);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    File destinationFile = new File(DIRECTORY, doc.getCareGiverID()+"_"+doc.getDocumentTypeID()+"_"+doc.getDocument()+"_" + fileExtension);
                    selectedFile.transferTo(destinationFile);
                    doc.setDocument(destinationFile.getAbsolutePath());

                } catch (Exception e) {
                    doc.setDocument(null);
                    throw new RuntimeException(e);
                }
            }
            CareGiverDocuments document = mapToEntity(doc);
            careGiverDocumentsRepository.save(document);
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
        careGiverDocumentsRepository.deleteById(ids);
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
