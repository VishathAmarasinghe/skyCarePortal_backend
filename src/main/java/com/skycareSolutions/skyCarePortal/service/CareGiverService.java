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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CareGiverService {

    @Autowired
    private CareGiverRepository careGiverRepository;

    @Autowired
    private CareGiverDocumentService careGiverDocumentService;

    @Autowired
    private CareGiverPaymentService careGiverPaymentService;

    @Autowired
    @Lazy
    private EmployeeService employeeService;

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


        return ResponseEntity.ok(mapToDTO(caregiver));
    }


    public ResponseEntity<CareGiverDTO> updateCareGiver(String id, CareGiverDTO dto) {
        if (!careGiverRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        CareGiver caregiver = mapToEntity(dto);
        caregiver.setCareGiverID(id);
        careGiverRepository.save(caregiver);
        return ResponseEntity.ok(mapToDTO(caregiver));
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
        List<Employee_address_DTO> employeeAddressDtos = new ArrayList<>();
        for (Employee_address address:entity.getEmployee().getEmployeeAddresses()){
            Employee_address_DTO employeeAddressDto = getEmployeeAddressDto(address);
            // add to the array
            employeeAddressDtos.add(employeeAddressDto);
        };

        employeeDTO.setEmployeeAddresses(employeeAddressDtos);
        employeeDTO.setStatus(entity.getEmployee().getStatus());
        employeeDTO.setEmail(entity.getEmployee().getEmail());

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
