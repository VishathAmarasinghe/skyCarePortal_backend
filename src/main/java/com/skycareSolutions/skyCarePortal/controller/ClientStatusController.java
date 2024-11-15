package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.ClientStatusDTO;
import com.skycareSolutions.skyCarePortal.service.ClientStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/clients-status")
public class ClientStatusController {
    private static final Logger logger = LoggerFactory.getLogger(ClientStatusController.class);

    @Autowired
    private ClientStatusService clientStatusService;

    // Create a new client status
    @PostMapping
    public ResponseEntity<ApiResponse> createClientStatus(@RequestBody ClientStatusDTO clientStatusDTO) {
        try {
            clientStatusService.createClientStatus(clientStatusDTO);
            return new ResponseEntity<>(new ApiResponse("Created client status successfully", true), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    // Get all client statuses
    @GetMapping
    public List<ClientStatusDTO> getAllClientStatuses() {
        return clientStatusService.getAllClientStatuses();
    }

    // Get client status by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClientStatusDTO> getClientStatusById(@PathVariable String id) {
        ClientStatusDTO clientStatusDTO = clientStatusService.getClientStatusById(id);
        return clientStatusDTO != null ? new ResponseEntity<>(clientStatusDTO, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Update an existing client status
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateClientStatus(@PathVariable String id, @RequestBody ClientStatusDTO clientStatusDTO) {
        try {
            clientStatusService.updateClientStatus(id, clientStatusDTO);
            return new ResponseEntity<>(new ApiResponse("Updated client status successfully", true), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete a client status by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteClientStatus(@PathVariable String id) {
        try {
            clientStatusService.deleteClientStatus(id);
            return new ResponseEntity<>(new ApiResponse("Deleted client status successfully", true), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }
}
