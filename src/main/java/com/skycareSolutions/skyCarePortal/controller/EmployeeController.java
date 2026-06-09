package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.repository.EmployeeRepository;
import com.skycareSolutions.skyCarePortal.service.ClientService;
import com.skycareSolutions.skyCarePortal.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@CrossOrigin("*")
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ClientService clientService;

    private static final Logger logger = LoggerFactory.getLogger(com.skycareSolutions.skyCarePortal.service.EmployeeService.class);

    @GetMapping
    public List<EmployeeDTO> getAllEmployees(){
        return employeeService.findAllEmployees();
    }

    @GetMapping("/clientAssignedCareGivers")
    public List<EmployeeDTO> getCareGiversAsEmployeesAssignToClient(@RequestParam String clientID){
        return employeeService.findCareGiversAsEmployeesAssignedWithClients(clientID);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addEmployee(
            @RequestPart("employeeData") String employeeData,
            @RequestParam(value = "profile_photo", required = false) MultipartFile profilePhoto) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            EmployeeDTO employee = objectMapper.readValue(employeeData, EmployeeDTO.class);
            employee.setStatus("Activated");
            // Check if the email already exists
            Optional<Employee> existingUser = employeeRepository.findByEmail(employee.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse("User Email Already Exists", false));
            }

            // Save new employee if email is unique
            employeeService.saveNewEmployee(employee, profilePhoto);

            return ResponseEntity
                    .ok(new ApiResponse("Employee Added Successfully", true));

        } catch (JsonProcessingException e) {
            // Handle JSON parsing errors
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse("Invalid employee data format", false));
        } catch (Exception e) {
            // Handle any other exceptions
            return ResponseEntity
                    .status(500)
                    .body(new ApiResponse("Error occurred while adding new employee", false));
        }
    }


    @GetMapping("/role/{role}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByState(@PathVariable String role){
        try {
            return ResponseEntity.ok(employeeService.getEmployeesByRole(role));
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    @GetMapping("/meta/allemployees")
    public ResponseEntity<List<MetaEmployeeDetailsDTO>> getAllEmployeeMetaDetails(){
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/lookup")
    public ResponseEntity<List<MetaEmployeeDetailsDTO>> lookupEmployees(
            @RequestParam String q,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(employeeService.lookupEmployees(q, role, limit));
    }

    @GetMapping("/meta/careGiverMapping")
    public ResponseEntity<Map<String, String>> getCareGiverEmployeeMapping() {
        // Directly return the map from the service
        Map<String, String> careGiverEmployeeMap = employeeService.getAllCareGiverEmployeeMapping();
        return ResponseEntity.ok(careGiverEmployeeMap);
    }


    @PatchMapping("/{employeeID}")
    public EmployeeDTO updateEmployee(@PathVariable String employeeID, @RequestPart("employeeData") String updatedEmployeeDTO, @RequestParam(value = "profile_photo", required = false) MultipartFile profilePhoto) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            EmployeeDTO employee = objectMapper.readValue(updatedEmployeeDTO,EmployeeDTO.class);
            employee.setEmployeeID(employeeID);
            return employeeService.updateEmployeeDetails(employeeID,employee,profilePhoto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/{employeeID}")
    public EmployeeDTO getSingleEmployee(@PathVariable("employeeID") String employeeID) {
        return employeeService.findOneEmployee(employeeID);
    }

    @PatchMapping("/{employeeID}/{state}")
    public ApiResponse updateEmployeeState(@PathVariable("employeeID") String employeeID,@PathVariable("state") String state){
        return employeeService.updateEmployeeState(employeeID,state);
    }

    @PostMapping("/update-basic-info")
    public ResponseEntity<String> updateUserBasicInfo(
            @RequestPart("employeeBasicInfo") String employeeBasicInfoUpdaterDTO,
            @RequestParam(name = "profilePic", required = false) MultipartFile profilePhoto){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            EmployeeBasicInfoUpdaterDTO employee = objectMapper.readValue(employeeBasicInfoUpdaterDTO, EmployeeBasicInfoUpdaterDTO.class);
            return ResponseEntity.ok(employeeService.updateEmployeeBasicInfoWithPanel(employee,profilePhoto));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            @RequestBody PasswordChangeDTO passwordChangeDTO
    ){
        try {
            if (passwordChangeDTO.getEmployeeID().substring(0,2).equalsIgnoreCase("CU")){
                return ResponseEntity.ok(clientService.updatePasswords(passwordChangeDTO));
            }else {
                return ResponseEntity.ok(employeeService.updatePasswords(passwordChangeDTO));
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }





}
