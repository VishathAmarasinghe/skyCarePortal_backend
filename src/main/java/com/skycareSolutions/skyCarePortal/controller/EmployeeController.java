package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.EmployeeDTO;
import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    private static final Logger logger = LoggerFactory.getLogger(com.skycareSolutions.skyCarePortal.service.EmployeeService.class);

    @GetMapping
    public List<EmployeeDTO> getAllEmployees(){
        return employeeService.findAllEmployees();
    }

    @PostMapping
    public ApiResponse addEmployee(@RequestPart("employeeData") String employeeData, @RequestParam("profile_photo") MultipartFile profilePhoto){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            EmployeeDTO employee = objectMapper.readValue(employeeData,EmployeeDTO.class);

            employeeService.saveNewEmployee(employee,profilePhoto);
            return new ApiResponse("Employee Added Successfully",true);

        } catch (Exception e) {
            throw new RuntimeException(e);
//            return new ApiResponse("Error occurred while adding new employee",false);
        }

    }

    @PatchMapping("/{employeeID}")
    public EmployeeDTO updateEmployee(@PathVariable String employeeID, @RequestPart("updatedEmployeeDTO") String updatedEmployeeDTO, @RequestParam(required = false) MultipartFile profilePhoto) {
        System.out.println("incomming employee id "+employeeID);
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





}
