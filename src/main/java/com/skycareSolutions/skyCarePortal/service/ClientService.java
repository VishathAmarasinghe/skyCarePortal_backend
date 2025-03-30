package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.ClientDTO;
import com.skycareSolutions.skyCarePortal.dto.ClientPhysicalAddressDTO;
import com.skycareSolutions.skyCarePortal.dto.ClientPostalAddressDTO;
import com.skycareSolutions.skyCarePortal.dto.PasswordChangeDTO;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.ClientRepository;
import com.skycareSolutions.skyCarePortal.repository.ClientStatusRepository;
import com.skycareSolutions.skyCarePortal.repository.ClientTypeRepository;
import com.skycareSolutions.skyCarePortal.repository.LanguageRepository;
import com.skycareSolutions.skyCarePortal.utils.AuthenticationUtils;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "Client_profile_Photos").toString();


    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private ClientTypeRepository clientTypeRepository;

    @Autowired
    private ClientStatusRepository clientStatusRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.external.service.frontendUrl}")
    private String frontendUrl;

    AuthenticationUtils authenticationUtils= new AuthenticationUtils();



    // Convert Client entity to ClientDTO
    private ClientDTO fromEntity(Client clientEntity,String type) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setClientID(clientEntity.getClientID());
        clientDTO.setFirstName(clientEntity.getFirstName());
        clientDTO.setLastName(clientEntity.getLastName());
        clientDTO.setPreferredName(clientEntity.getPreferredName());
        clientDTO.setEmail(clientEntity.getEmail());
        clientDTO.setGender(clientEntity.getGender());
        clientDTO.setBirthday(clientEntity.getBirthday());
        clientDTO.setJoinDate(clientEntity.getJoinDate());
        clientDTO.setAboutMe(clientEntity.getAboutMe());
        clientDTO.setInterests(clientEntity.getInterests());
        clientDTO.setDislikes(clientEntity.getDislikes());
        clientDTO.setEmergencyUser(clientEntity.getEmergencyUserName());
        clientDTO.setEmergencyPhoneNo(clientEntity.getEmergencyPhoneNo());
        clientDTO.setProfilePhoto(clientEntity.getProfilePhoto());
        clientDTO.setReferenceNo(clientEntity.getReferenceNo());
        clientDTO.setStatus(clientEntity.getStatus());

        // Map physicalAddress and postalAddress
        if (clientEntity.getPhysicalAddress() != null) {
            ClientPhysicalAddressDTO physicalAddressDTO = new ClientPhysicalAddressDTO();
            physicalAddressDTO.setId(clientEntity.getPhysicalAddress().getId());
            physicalAddressDTO.setAddress(clientEntity.getPhysicalAddress().getAddress());
            physicalAddressDTO.setCity(clientEntity.getPhysicalAddress().getCity());
            physicalAddressDTO.setState(clientEntity.getPhysicalAddress().getState());
            physicalAddressDTO.setCountry(clientEntity.getPhysicalAddress().getCountry());
            physicalAddressDTO.setPostalCode(clientEntity.getPhysicalAddress().getPostalCode());
            physicalAddressDTO.setLongitude(clientEntity.getPhysicalAddress().getLongitude());
            physicalAddressDTO.setLatitude(clientEntity.getPhysicalAddress().getLatitude());
            clientDTO.setPhysicalAddress(physicalAddressDTO);
        }

        if (clientEntity.getPostalAddress() != null) {
            ClientPostalAddressDTO postalAddressDTO = new ClientPostalAddressDTO();
            postalAddressDTO.setId(clientEntity.getPostalAddress().getId());
            postalAddressDTO.setAddress(clientEntity.getPostalAddress().getAddress());
            postalAddressDTO.setCity(clientEntity.getPostalAddress().getCity());
            postalAddressDTO.setState(clientEntity.getPostalAddress().getState());
            postalAddressDTO.setCountry(clientEntity.getPostalAddress().getCountry());
            postalAddressDTO.setPostalCode(clientEntity.getPostalAddress().getPostalCode());
            postalAddressDTO.setLongitude(clientEntity.getPostalAddress().getLongitude());
            postalAddressDTO.setLatitude(clientEntity.getPostalAddress().getLatitude());
            clientDTO.setPostalAddress(postalAddressDTO);
        }

        if (type.equalsIgnoreCase("values")){
            clientDTO.setClientLanguages(clientEntity.getClientLanguages().stream().map(ClientLanguage ->ClientLanguage.getLanguage().getLanguage()).toList());
            clientDTO.setClientClassifications(clientEntity.getClientClassifications().stream().map(ClientClassification->ClientClassification.getClassification().getClassificationName()).toList());
            clientDTO.setClientType(clientEntity.getClientType().getName());
            clientDTO.setClientStatus(clientEntity.getClientStatus().getStatus());

        }else{
            clientDTO.setClientClassifications(clientEntity.getClientClassifications().stream().map(ClientClassification->ClientClassification.getClassification().getClassificationID()).toList());
            clientDTO.setClientLanguages(clientEntity.getClientLanguages().stream().map(ClientLanguage ->ClientLanguage.getLanguage().getLanguageID()).toList());
            clientDTO.setClientType(clientEntity.getClientType().getClientTypeID());
            clientDTO.setClientStatus(clientEntity.getClientStatus().getClientStatusID());
        }
        // Map other lists and references
        clientDTO.setPhoneNumbers(clientEntity.getPhoneNumbers().stream().map(ClientPhoneNo->ClientPhoneNo.getId().getPhoneNo()).toList());


        return clientDTO;
    }

    // Convert ClientDTO to Client entity
    private Client toEntity(ClientDTO clientDTO) {

        Client clientEntity = new Client();
        clientEntity.setClientID(clientDTO.getClientID());
        clientEntity.setFirstName(clientDTO.getFirstName());
        clientEntity.setLastName(clientDTO.getLastName());
        clientEntity.setPreferredName(clientDTO.getPreferredName());
        clientEntity.setEmail(clientDTO.getEmail());
        clientEntity.setGender(clientDTO.getGender());
        clientEntity.setBirthday(clientDTO.getBirthday());
        clientEntity.setJoinDate(clientDTO.getJoinDate());
        clientEntity.setAboutMe(clientDTO.getAboutMe());
        clientEntity.setEmergencyPhoneNo(clientDTO.getEmergencyPhoneNo());
        clientEntity.setEmergencyUserName(clientDTO.getEmergencyUser());
        clientEntity.setInterests(clientDTO.getInterests());
        clientEntity.setDislikes(clientDTO.getDislikes());
        clientEntity.setProfilePhoto(clientDTO.getProfilePhoto());
        clientEntity.setReferenceNo(clientDTO.getReferenceNo());
        clientEntity.setStatus(clientDTO.getStatus());

        // Map physicalAddress and postalAddress
        if (clientDTO.getPhysicalAddress() != null) {
            ClientPhysicalAddress physicalAddress = new ClientPhysicalAddress();
            physicalAddress.setId(clientDTO.getPhysicalAddress().getId());
            physicalAddress.setAddress(clientDTO.getPhysicalAddress().getAddress());
            physicalAddress.setCity(clientDTO.getPhysicalAddress().getCity());
            physicalAddress.setState(clientDTO.getPhysicalAddress().getState());
            physicalAddress.setCountry(clientDTO.getPhysicalAddress().getCountry());
            physicalAddress.setPostalCode(clientDTO.getPhysicalAddress().getPostalCode());
            physicalAddress.setLongitude(clientDTO.getPhysicalAddress().getLongitude());
            physicalAddress.setLatitude(clientDTO.getPhysicalAddress().getLatitude());
            clientEntity.setPhysicalAddress(physicalAddress);
        }

        if (clientDTO.getPostalAddress() != null) {
            ClientPostalAddress postalAddress = new ClientPostalAddress();
            postalAddress.setId(clientDTO.getPostalAddress().getId());
            postalAddress.setAddress(clientDTO.getPostalAddress().getAddress());
            postalAddress.setCity(clientDTO.getPostalAddress().getCity());
            postalAddress.setState(clientDTO.getPostalAddress().getState());
            postalAddress.setCountry(clientDTO.getPostalAddress().getCountry());
            postalAddress.setPostalCode(clientDTO.getPostalAddress().getPostalCode());
            postalAddress.setLongitude(clientDTO.getPostalAddress().getLongitude());
            postalAddress.setLatitude(clientDTO.getPostalAddress().getLatitude());
            clientEntity.setPostalAddress(postalAddress);
        }

        // Map other lists and references
        System.out.println("languages "+clientDTO.getClientLanguages().toString());
        clientEntity.setClientLanguages(
                clientDTO.getClientLanguages().stream()
                        .map(languageId -> {
                            Language language = languageRepository.findById(languageId).orElseThrow(() ->
                                    new RuntimeException("Language not found: " + languageId)
                            );
                            return new ClientLanguage(new ClientLanguageId(clientEntity.getClientID(),languageId),clientEntity, language); // Pass Client and Language entities
                        })
                        .toList()
        );

        clientEntity.setPhoneNumbers(
                clientDTO.getPhoneNumbers().stream()
                        .distinct() // Remove duplicate phone numbers
                        .map(phoneNumber -> new ClientPhoneNo(
                                new ClientPhoneNoId(clientEntity.getClientID(), phoneNumber),
                                clientEntity))
                        .toList()
        );


        System.out.println("phone no "+clientDTO.getClientType().toString());
        Optional<ClientType> clientType = clientTypeRepository.findById(clientDTO.getClientType());
        if (clientType.isPresent()){
            clientEntity.setClientType(clientType.get());
        }

        System.out.println("phone no "+clientDTO.getClientStatus().toString());
        Optional<ClientStatus> clientStatusValue = clientStatusRepository.findById(clientDTO.getClientStatus());
        if (clientStatusValue.isPresent()) {
            clientEntity.setClientStatus(clientStatusValue.get());
        }

        clientEntity.setClientClassifications(
                clientDTO.getClientClassifications().stream()
                        .map(classificationID -> {
                            // Create the ClientClassificationID
                            ClientClassificationID classificationId = new ClientClassificationID(clientEntity.getClientID(), classificationID);

                            // Print the ClientClassificationID to verify it's being created
                            System.out.println("Creating ClientClassificationID: " + classificationId.getClientID());

                            // Create and return ClientClassification
                            return new ClientClassification(
                                    classificationId,
                                    clientEntity,
                                    new Classification(classificationID) // Assuming Classification has a constructor taking classificationID
                            );
                        })
                        .toList()
        );
        return clientEntity;
    }

    // Get all clients
    public List<ClientDTO> getAllClients() {
        return clientRepository.getAllClients().stream()
                .map(client->fromEntity(client,"values"))
                .collect(Collectors.toList());
    }

    // Create a new client
    public ClientDTO createClient(ClientDTO clientDTO, MultipartFile profilePhoto) {
        // Check if email already exists
        String lastClientID = clientRepository.getLastIdOfClient();
        String newClientID = Micro_utils.idGenerator("client",lastClientID);
        clientDTO.setClientID(newClientID);

        try {
            if (profilePhoto!=null){
                String originalFileName = profilePhoto.getOriginalFilename();
                String fileExtension = originalFileName;
                if (originalFileName != null && originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }
                System.out.println("original file name "+originalFileName);
                File directory = new File(DIRECTORY);
                if(!directory.exists()){
                    directory.mkdirs();
                }
                File destinationFile = new File(DIRECTORY, newClientID + fileExtension);
                profilePhoto.transferTo(destinationFile);
                clientDTO.setProfilePhoto(destinationFile.getAbsolutePath());

            }
            Client clientEntity = toEntity(clientDTO);
            clientRepository.save(clientEntity);
            return fromEntity(clientEntity,"ids");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Get client by ID
    public ClientDTO getClientById(String clientID) {
        Optional<Client> clientEntity = clientRepository.findById(clientID);
        return clientEntity.map(client->fromEntity(client,"values")).orElseThrow(() -> new RuntimeException("Client not found"));
    }


    //update client activation status
    public String updateClientStatus(String clientID,String status) {
        Optional<Client> clientEntity = clientRepository.findById(clientID);
        if (clientEntity.isPresent()){
            Client client = clientEntity.get();
            client.setStatus(status);
            if (status.equalsIgnoreCase("Activated")){
                String password = Micro_utils.generateRandomPassword(10);
                client.setPassword(authenticationUtils.hashPassword(password));
                CompletableFuture.runAsync(()->emailService.generateWelcomeLoginEmail(client.getFirstName(),client.getEmail(),password,frontendUrl));
            }
            clientRepository.save(client);
            return "updated successfully";
        }else{
            throw new RuntimeException();
        }
    }



    public Client getClientByIdWithEntitiy(String clientID) {
        Optional<Client> clientEntity = clientRepository.findById(clientID);
        return clientEntity.orElseThrow(() -> new RuntimeException("Client not found"));
    }

    // Update an existing client
    public ClientDTO updateClient(String clientID, ClientDTO clientDTO) {
        Optional<Client> existingClient = clientRepository.findById(clientID);
        if (existingClient.isPresent()) {
            Client pastClient= existingClient.get();
            pastClient.getPhoneNumbers().clear();
            pastClient.getClientClassifications().clear();
            pastClient.getClientLanguages().clear();
            clientRepository.save(pastClient);

            Client updatedClient = toEntity(clientDTO);
            updatedClient.setClientID(clientID);
            clientRepository.save(updatedClient);
            return fromEntity(updatedClient,"values");
        } else {
            throw new RuntimeException("Client not found");
        }
    }

    public String updatePasswords(PasswordChangeDTO passwordChangeDTO){
        Optional<Client> employeeDTO =  clientRepository.findById(passwordChangeDTO.getEmployeeID());
        if (employeeDTO.isPresent()){
            Client exisitingEmployee = employeeDTO.get();
            if (passwordChangeDTO.getCurrentPassword()!=null && passwordChangeDTO.getCurrentPassword()!="") {
                if (!authenticationUtils.matchPassword(passwordChangeDTO.getCurrentPassword(), exisitingEmployee.getPassword())) {
                    throw new RuntimeException("Old Password is incorrect");
                }

                String newPassword = authenticationUtils.hashPassword(passwordChangeDTO.getNewPassword());
                exisitingEmployee.setPassword(newPassword);
                clientRepository.save(exisitingEmployee);
            }
        }
        return "Password Changed Successfully";
    }

    // Delete a client by ID
    public void deleteClient(String clientID) {
        Optional<Client> client = clientRepository.findById(clientID);
        if (client.isPresent()) {
            clientRepository.delete(client.get());
        } else {
            throw new RuntimeException("Client not found");
        }
    }
}
