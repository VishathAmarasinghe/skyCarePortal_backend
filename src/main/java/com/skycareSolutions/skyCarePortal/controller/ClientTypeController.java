package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.ClientTypeDTO;
import com.skycareSolutions.skyCarePortal.service.ClientTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/client-type")
public class ClientTypeController {

    @Autowired
    private ClientTypeService clientTypeService;

    // Create a new ClientType
    @PostMapping
    public ResponseEntity<ApiResponse> createClientType(@RequestBody ClientTypeDTO clientTypeDTO) {
        try {
            clientTypeService.createClientType(clientTypeDTO);
            return new ResponseEntity<>(new ApiResponse("Created Client Type successfully", true), HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("error "+e);
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    // Get all ClientTypes
    @GetMapping
    public List<ClientTypeDTO> getAllClientTypes() {
        return clientTypeService.getAllClientTypes();
    }

    // Get a specific ClientType by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClientTypeDTO> getClientTypeById(@PathVariable Long id) {
        ClientTypeDTO clientType = clientTypeService.getClientTypeById(id);
        return clientType != null ?
                new ResponseEntity<>(clientType, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Update a ClientType
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateClientType(@PathVariable String id, @RequestBody ClientTypeDTO clientTypeDTO) {
        try {
            clientTypeService.updateClientType(id, clientTypeDTO);
            return new ResponseEntity<>(new ApiResponse("Updated Client Type successfully", true), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete a ClientType by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteClientType(@PathVariable Long id) {
        try {
            clientTypeService.deleteClientType(id);
            return new ResponseEntity<>(new ApiResponse("Deleted Client Type successfully", true), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }
}
