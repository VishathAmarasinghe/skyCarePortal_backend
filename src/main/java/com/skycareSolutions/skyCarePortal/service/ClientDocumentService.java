package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.ClientDocumentDTO;
import com.skycareSolutions.skyCarePortal.dto.ResourceDocumentDTO;
import com.skycareSolutions.skyCarePortal.model.Client;
import com.skycareSolutions.skyCarePortal.model.ClientDocuments;
import com.skycareSolutions.skyCarePortal.repository.ClientDocumentRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientDocumentService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "ClientDocuments").toString();

    @Autowired
    private  ClientDocumentRepository clientDocumentRepository;

    @Autowired
    @Lazy
    private ClientService clientService;


    private ClientDocumentDTO toDTO(ClientDocuments clientDocuments){
        ClientDocumentDTO clientDocumentDTO = new ClientDocumentDTO();
        clientDocumentDTO.setClientDocumentID(clientDocuments.getClientDocumentID());
        clientDocumentDTO.setDocumentLocation(clientDocuments.getDocumentLocation());
        clientDocumentDTO.setDocumentName(clientDocuments.getDocumentName());
        clientDocumentDTO.setShareType(clientDocuments.getShareType());
        clientDocumentDTO.setCreatedDate(clientDocuments.getCreatedDate());
        clientDocumentDTO.setClientId(clientDocuments.getClientid().getClientID());
        return clientDocumentDTO;
    }

    private ClientDocuments toEntity(ClientDocumentDTO clientDocumentDTO){
        ClientDocuments clientDocuments = new ClientDocuments();
        clientDocuments.setClientDocumentID(clientDocumentDTO.getClientDocumentID());
        clientDocuments.setDocumentName(clientDocumentDTO.getDocumentName());
        clientDocuments.setShareType(clientDocumentDTO.getShareType());
        clientDocuments.setDocumentLocation(clientDocumentDTO.getDocumentLocation());

        Client client =clientService.getClientByIdWithEntitiy(clientDocumentDTO.getClientId());
        clientDocuments.setClientid(client);
        return clientDocuments;
    }

    // Save multiple ClientDocumentDTOs
    public void saveClientDocuments(List<ClientDocumentDTO> documents, List<MultipartFile> files) {

        for(MultipartFile file:files){
            ClientDocumentDTO matchingDocument = documents.stream()
                    .filter(doc -> doc.getDocumentName().equalsIgnoreCase(file.getOriginalFilename()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No matching document found for file: " + file.getOriginalFilename()));

            if (matchingDocument!=null){
                String lastClientDocumentID = clientDocumentRepository.getLastIdOfClientDocument();
                String newClientDocumentID = Micro_utils.idGenerator("clientDocuments",lastClientDocumentID);
                matchingDocument.setClientDocumentID(newClientDocumentID);

                try {
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

                        File destinationFile = new File(DIRECTORY, newClientDocumentID+"_"+originalFileName+ fileExtension);
                        if (destinationFile.exists()) {
                            // Delete the existing file
                            boolean deleted = destinationFile.delete();
                            if (!deleted) {
                                throw new RuntimeException("Failed to delete the existing file: " + destinationFile.getName());
                            }
                        }

                        file.transferTo(destinationFile);
                        matchingDocument.setDocumentLocation(destinationFile.getAbsolutePath());
                        matchingDocument.setDocumentName(newClientDocumentID+"_"+originalFileName+ fileExtension);

                        clientDocumentRepository.save(toEntity(matchingDocument));
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    public ClientDocumentDTO getClientDocumentById(String id) {
        return clientDocumentRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("ClientDocument not found"));
    }

    public List<ClientDocumentDTO> getAllClientDocuments() {
        return clientDocumentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ClientDocumentDTO> getAllDocumentsRelatedTOClient(String clientID){
        return clientDocumentRepository.getDocumentsByClientID(clientService.getClientByIdWithEntitiy(clientID)).stream()
                .map(this::toDTO).toList();
    }

    public void deleteClientDocument(String id) {
        Optional<ClientDocuments> clientDocument = clientDocumentRepository.findById(id);
        if (clientDocument.isPresent()){
            ClientDocuments doc = clientDocument.get();
            clientDocumentRepository.deleteById(id);
            try {
                File destinationFile = new File(DIRECTORY, doc.getDocumentName());
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

    }
}
