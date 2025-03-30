package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycareSolutions.skyCarePortal.dto.ClientDTO;
import com.skycareSolutions.skyCarePortal.dto.PasswordChangeDTO;
import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.service.ClientService;
import com.skycareSolutions.skyCarePortal.service.EmailService;
import com.skycareSolutions.skyCarePortal.service.JobAssignerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private EmailService emailService;

    @Autowired
    @Lazy
    private JobAssignerService jobAssignerService;

    // Get all clients
    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<ClientDTO> clients = clientService.getAllClients();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    // Get client by ID
    @GetMapping("/{clientID}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable String clientID) {
        ClientDTO client = clientService.getClientById(clientID);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<String> updateClientActivationStatus(@PathVariable String id, @RequestParam String status){
        return  new ResponseEntity<>(clientService.updateClientStatus(id,status),HttpStatus.OK);
    }

    @GetMapping("/careGiverAssignedClients/{id}")
    public ResponseEntity<List<ClientDTO>> careGiverAssignedClientList(@PathVariable String id){
        List<String> clientIDList = jobAssignerService.getAllClientIDListCareGiverAssociated(id);
        List<ClientDTO> clientList = new ArrayList<>();
        for (String clientID:clientIDList){
            clientList.add(clientService.getClientById(clientID));
        }
        return ResponseEntity.ok(clientList);
    }

    // Create a new client
    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@RequestPart("clientData") String clientData, @RequestParam(value = "profile_photo",required = false) MultipartFile profilePhoto) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            ClientDTO clientDTO = mapper.readValue(clientData,ClientDTO.class);
            ClientDTO createdClient = clientService.createClient(clientDTO,profilePhoto);
            return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // Update an existing client
    @PutMapping("/{clientID}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable String clientID, @RequestBody ClientDTO clientDTO) {
        ClientDTO updatedClient = clientService.updateClient(clientID, clientDTO);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }

    // Delete a client
    @DeleteMapping("/{clientID}")
    public ResponseEntity<Void> deleteClient(@PathVariable String clientID) {
        clientService.deleteClient(clientID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
