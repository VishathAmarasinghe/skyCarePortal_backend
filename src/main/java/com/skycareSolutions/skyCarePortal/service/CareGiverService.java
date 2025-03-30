package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.CareGiverRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CareGiverService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "careGiverDocuments").toString();

    @Autowired
    private CareGiverRepository careGiverRepository;

    @Autowired
    private CareGiverDocumentService careGiverDocumentService;

    @Autowired
    private CareGiverPaymentService careGiverPaymentService;

    @Autowired
    @Lazy
    private EmployeeService employeeService;

    @Autowired
    @Lazy
    private EmailService emailService;

    public List<CareGiverDTO> getAllCareGivers() {
        List<CareGiverDTO> caregivers = careGiverRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return caregivers;
    }

    public ResponseEntity<CareGiverDTO> getCareGiverById(String id) {
        CareGiver caregiver = careGiverRepository.findById(id).orElse(null);
        if (caregiver == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToDTO(caregiver));
    }

    public CareGiver getCareGiverByIdWithEntity(String id) {
        CareGiver caregiver = careGiverRepository.findById(id).orElse(null);
        return caregiver;
    }

    public CareGiverDTO getCareGiverByEmployee(String employeeID) {
        Employee employee = employeeService.findOneEmployeeWithEntity(employeeID);
        if (employee!=null){
            CareGiver caregiver = careGiverRepository.getCareGiverByEmployee(employee);
            return mapToDTO(caregiver);
        }else{
             throw new RuntimeException();
        }
    }


    public ResponseEntity<CareGiverDTO> addCareGiver(CareGiverDTO dto, MultipartFile profilePhoto,List<MultipartFile> documents) {
        dto.getEmployee().setAccessRole("CareGiver");
        dto.getEmployee().setStatus("Pending");
        Employee employee = employeeService.saveNewEmployee(dto.getEmployee(),profilePhoto);
        String lastCaregiverID = careGiverRepository.getLastIdOfcareGiver();
        String newCaregiverID = Micro_utils.idGenerator("careGiver",lastCaregiverID);
        dto.setCareGiverID(newCaregiverID);
        dto.getEmployee().setEmployeeID(employee.getEmployeeID());
        CareGiver caregiver = mapToEntity(dto);
        careGiverRepository.save(caregiver);

        for (CareGiverDocumentsDTO docs:dto.getCareGiverDocuments()){
            docs.setCareGiverID(newCaregiverID);
        }
        for (CareGiverPaymentsDTO payment:dto.getCareGiverPayments()){
            payment.setCareGiverID(newCaregiverID);
        }
        careGiverPaymentService.addPayment(dto.getCareGiverPayments());
        careGiverDocumentService.addDocument(dto.getCareGiverDocuments(),documents);

        CompletableFuture.runAsync(()->emailService.generateDocumentSubmissionAcknowledgmentEmail(dto.getEmployee().getFirstName(),dto.getEmployee().getEmail()));

        return ResponseEntity.ok(mapToDTO(caregiver));
    }


    public ResponseEntity<CareGiverDTO> updateCareGiver(String id, CareGiverDTO dto, MultipartFile profilePhoto,List<MultipartFile> documents) {
        if (!careGiverRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        CareGiver caregiver = mapToEntity(dto);
        caregiver.setCareGiverID(id);
        for (CareGiverPaymentsDTO paymentsDTO:dto.getCareGiverPayments()){
            paymentsDTO.setCareGiverID(id);
        }
        employeeService.updateEmployeeDetails(dto.getEmployee().getEmployeeID(),dto.getEmployee(),profilePhoto);
        List<CareGiverDocumentsDTO>  exisitingDocuments = careGiverDocumentService.getDocumentsByCareGiverId(id);

        List<CareGiverPaymentsDTO> exisitingCareGiverPayments = careGiverPaymentService.getPaymentsByCareGiverId(id);
        if (exisitingCareGiverPayments.isEmpty() && !dto.getCareGiverPayments().isEmpty()){
            careGiverPaymentService.addPayment(dto.getCareGiverPayments());

        }else{
            for (CareGiverPaymentsDTO payment:dto.getCareGiverPayments()){
                Optional<CareGiverPaymentsDTO> paymentValue = exisitingCareGiverPayments.stream().filter(sm->payment.getPaymentTypeID().equalsIgnoreCase(sm.getPaymentTypeID())).findFirst();
                if (paymentValue.isPresent()){
                    careGiverPaymentService.updatePayment(id,paymentValue.get().getPaymentTypeID(),payment);
                }else{
                    List<CareGiverPaymentsDTO> paymentList = new ArrayList<>();
                    paymentList.add(payment);
                    careGiverPaymentService.addPayment(paymentList);
                }
            }
        }
        for (CareGiverDocumentsDTO doc:dto.getCareGiverDocuments()){
            if (doc.getDocument().equalsIgnoreCase("")){
                for (CareGiverDocumentsDTO exisitDoc:exisitingDocuments){
                    if (exisitDoc.getDocumentTypeID().equalsIgnoreCase(doc.getDocumentTypeID())){
                        try{
                            File existingFile = new File(exisitDoc.getDocument());
                            if (existingFile.exists()) {
                                existingFile.delete();
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        careGiverDocumentService.deleteDocument(exisitDoc.getCareGiverID(),exisitDoc.getDocumentTypeID());
                    }
                }
            }
        }
        for (CareGiverDocumentsDTO newDocs:dto.getCareGiverDocuments()){
            CareGiverDocumentsDTO eixis = exisitingDocuments.stream().filter(row->row.getDocumentTypeID().equalsIgnoreCase(newDocs.getDocumentTypeID())).findFirst().orElse(null);
            if (eixis==null){
                List<CareGiverDocumentsDTO> documentList= new ArrayList<>();
                documentList.add(newDocs);
                careGiverDocumentService.addDocument(documentList,documents);

            }
        }

        for (CareGiverDocumentsDTO exisitDoc:exisitingDocuments){

            CareGiverDocumentsDTO inputDoc = dto.getCareGiverDocuments()
                    .stream()
                    .filter(row -> exisitDoc != null &&
                            row.getDocumentTypeID() != null &&
                            row.getDocumentTypeID().equalsIgnoreCase(exisitDoc.getDocumentTypeID()))
                    .findFirst()
                    .orElse(null);
            if (inputDoc!=null){
                if (!inputDoc.getDocument().equalsIgnoreCase(exisitDoc.getDocument())){
                    try {
                        File existingFile = new File(exisitDoc.getDocument());
                        if (existingFile.exists()) {
                            existingFile.delete();
                        }
                        // Add new file
                        if (documents!=null && documents.size()>0){
                            MultipartFile newFile = documents.stream()
                                    .filter(file -> file.getOriginalFilename() != null &&
                                            file.getOriginalFilename().equalsIgnoreCase(inputDoc.getDocument()))
                                    .findFirst()
                                    .orElse(null);

                            if (newFile != null) {
                                try {
                                    String originalFileName = newFile.getOriginalFilename();
                                    String fileExtension = originalFileName != null && originalFileName.contains(".")
                                            ? originalFileName.substring(originalFileName.lastIndexOf("."))
                                            : "";

                                    File directory = new File(DIRECTORY);
                                    if (!directory.exists()) {
                                        directory.mkdirs();
                                    }

                                    File newFilePath = new File(directory,
                                            inputDoc.getCareGiverID() + "_" +
                                                    inputDoc.getDocumentTypeID() + "_" +
                                                    originalFileName + fileExtension);
                                    newFile.transferTo(newFilePath);
                                    inputDoc.setDocument(newFilePath.getAbsolutePath());
                                    careGiverDocumentService.updateDocument(id,inputDoc.getDocumentTypeID(),inputDoc);
                                } catch (Exception e) {
                                    throw new RuntimeException("Error uploading new document", e);
                                }
                            }
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }else{
                System.out.println("null file returned "+exisitDoc.getDocumentTypeID());

            }
        }

        careGiverRepository.save(caregiver);
        return ResponseEntity.ok(mapToDTO(caregiver));
    }

    public ResponseEntity<String> updateCareGiverDocuments(String id, List<CareGiverDocumentsDTO> dto,List<MultipartFile> documents) {
        Employee employee = employeeService.findOneEmployeeWithEntity(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        CareGiver careGiver = careGiverRepository.getCareGiverByEmployee(employee);
        if (careGiver == null) {
            return ResponseEntity.notFound().build();
        }

        id = careGiver.getCareGiverID();

        List<CareGiverDocumentsDTO>  exisitingDocuments = careGiverDocumentService.getDocumentsByCareGiverId(id);

        for (CareGiverDocumentsDTO doc:dto){
            if (doc.getDocument().equalsIgnoreCase("")){
                for (CareGiverDocumentsDTO exisitDoc:exisitingDocuments){
                    if (exisitDoc.getDocumentTypeID().equalsIgnoreCase(doc.getDocumentTypeID())){
                        try{
                            File existingFile = new File(exisitDoc.getDocument());
                            if (existingFile.exists()) {
                                existingFile.delete();
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        careGiverDocumentService.deleteDocument(exisitDoc.getCareGiverID(),exisitDoc.getDocumentTypeID());
                    }
                }
            }
        }

        for (CareGiverDocumentsDTO newDocs:dto){
            CareGiverDocumentsDTO eixis = exisitingDocuments.stream().filter(row->row.getDocumentTypeID().equalsIgnoreCase(newDocs.getDocumentTypeID())).findFirst().orElse(null);
            if (eixis==null){
                List<CareGiverDocumentsDTO> documentList= new ArrayList<>();
                documentList.add(newDocs);
                careGiverDocumentService.addDocument(documentList,documents);

            }
        }

        for (CareGiverDocumentsDTO exisitDoc:exisitingDocuments){

            CareGiverDocumentsDTO inputDoc = dto
                    .stream()
                    .filter(row -> exisitDoc != null &&
                            row.getDocumentTypeID() != null &&
                            row.getDocumentTypeID().equalsIgnoreCase(exisitDoc.getDocumentTypeID()))
                    .findFirst()
                    .orElse(null);
            if (inputDoc!=null){
                if (!inputDoc.getDocument().equalsIgnoreCase(exisitDoc.getDocument())){
                    try {
                        File existingFile = new File(exisitDoc.getDocument());
                        if (existingFile.exists()) {
                            existingFile.delete();
                        }
                        // Add new file
                        if (documents!=null && documents.size()>0){
                            MultipartFile newFile = documents.stream()
                                    .filter(file -> file.getOriginalFilename() != null &&
                                            file.getOriginalFilename().equalsIgnoreCase(inputDoc.getDocument()))
                                    .findFirst()
                                    .orElse(null);

                            if (newFile != null) {
                                try {
                                    String originalFileName = newFile.getOriginalFilename();
                                    String fileExtension = originalFileName != null && originalFileName.contains(".")
                                            ? originalFileName.substring(originalFileName.lastIndexOf("."))
                                            : "";

                                    File directory = new File(DIRECTORY);
                                    if (!directory.exists()) {
                                        directory.mkdirs();
                                    }

                                    File newFilePath = new File(directory,
                                            inputDoc.getCareGiverID() + "_" +
                                                    inputDoc.getDocumentTypeID() + "_" +
                                                    originalFileName + fileExtension);
                                    newFile.transferTo(newFilePath);
                                    inputDoc.setDocument(newFilePath.getAbsolutePath());
                                    careGiverDocumentService.updateDocument(id,inputDoc.getDocumentTypeID(),inputDoc);
                                } catch (Exception e) {
                                    throw new RuntimeException("Error uploading new document", e);
                                }
                            }
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }else{
                System.out.println("null file returned "+exisitDoc.getDocumentTypeID());


            }
        }
        return ResponseEntity.ok("Updated Documents");
    }

    public ResponseEntity<Void> deleteCareGiver(String id) {
        if (!careGiverRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        careGiverRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }



    private CareGiverDTO mapToDTO(CareGiver entity) {

        CareGiverDTO dto = new CareGiverDTO();
        dto.setCareGiverID(entity.getCareGiverID());
        // employee data
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeID(entity.getEmployee().getEmployeeID());
        employeeDTO.setProfile_photo(entity.getEmployee().getProfile_photo());
        employeeDTO.setAccessRole(entity.getEmployee().getAccessRole());
        employeeDTO.setJoinDate(entity.getEmployee().getJoinDate());
        List<Employee_address_DTO> employeeAddressDtos = new ArrayList<>();
        for (Employee_address address:entity.getEmployee().getEmployeeAddresses()){
            Employee_address_DTO employeeAddressDto = getEmployeeAddressDto(address);
            // add to the array
            employeeAddressDtos.add(employeeAddressDto);
        };

        employeeDTO.setEmployeeAddresses(employeeAddressDtos);
        employeeDTO.setStatus(entity.getEmployee().getStatus());
        employeeDTO.setEmail(entity.getEmployee().getEmail());
        employeeDTO.setEmergencyUser(entity.getEmployee().getEmergencyUser());
        employeeDTO.setEmergencyPhoneNo(entity.getEmployee().getEmergencyPhoneNo());

        List<String> employeePhoneNo = new ArrayList<>();
        for (Employee_phoneNo phoneNo:entity.getEmployee().getEmployeePhoneNo()){
            employeePhoneNo.add(phoneNo.getId().getPhoneNo().toString());
        }

        employeeDTO.setEmployeePhoneNo(employeePhoneNo);
        List<String> jobRoleArray = new ArrayList<>();

        for (Employee_job_Roles jobs:entity.getEmployee().getEmployeeJobRoles()){
            jobRoleArray.add(jobs.getId().getJobRole());
        }
        employeeDTO.setEmployeeJobRoles(jobRoleArray);
        employeeDTO.setFirstName(entity.getEmployee().getFirstName());
        employeeDTO.setLastName(entity.getEmployee().getLastName());

        dto.setEmployee(employeeDTO);
        dto.setStatus(entity.getStatus());

        dto.setCareGiverPayments(careGiverPaymentService.getPaymentsByCareGiverId(entity.getCareGiverID()));

        List<CareGiverDocumentsDTO> documentsDTOList = careGiverDocumentService.getDocumentsByCareGiverId(entity.getCareGiverID());
        dto.setCareGiverDocuments(documentsDTOList);
        return dto;
    }

    // set employee address.
    private static Employee_address_DTO getEmployeeAddressDto(Employee_address address) {
        Employee_address_DTO employeeAddressDto = new Employee_address_DTO();
        employeeAddressDto.setAddress(address.getAddress());
        employeeAddressDto.setCountry(address.getCountry());
        employeeAddressDto.setState(address.getState());
        employeeAddressDto.setCity(address.getCity());
        employeeAddressDto.setPostal_code(address.getPostal_code());
        employeeAddressDto.setLatitude(address.getId().getLatitude());
        employeeAddressDto.setLongitude(address.getId().getLongitude());
        return employeeAddressDto;
    }

    private CareGiver mapToEntity(CareGiverDTO dto) {
        CareGiver entity = new CareGiver();
        entity.setCareGiverID(dto.getCareGiverID());
        entity.setEmployee(employeeService.findOneEmployeeWithEntity(dto.getEmployee().getEmployeeID()));
        entity.setStatus(dto.getStatus());
        return entity;
    }
}
