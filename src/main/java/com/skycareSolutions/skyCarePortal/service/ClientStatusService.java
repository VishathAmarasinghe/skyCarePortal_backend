package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.ClientStatusDTO;
import com.skycareSolutions.skyCarePortal.model.ClientStatus;
import com.skycareSolutions.skyCarePortal.repository.ClientStatusRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientStatusService {

    @Autowired
    private ClientStatusRepository clientStatusRepository;

    // Create a new client status
    public void createClientStatus(ClientStatusDTO clientStatusDTO) throws Exception {
        if (clientStatusRepository.findByClientStatusName(clientStatusDTO.getStatus()) != null) {
            throw new Exception("A client status with this name already exists.");
        }

        String lastClientStatusID = clientStatusRepository.getLastIdOfClientStatus();
        String newClientStatusID = Micro_utils.idGenerator("clientStatus", lastClientStatusID);
        clientStatusDTO.setClientStatusID(newClientStatusID);

        ClientStatus clientStatus = new ClientStatus();
        clientStatus.setClientStatusID(clientStatusDTO.getClientStatusID());
        clientStatus.setStatus(clientStatusDTO.getStatus());

        clientStatusRepository.save(clientStatus);
    }

    // Get all client statuses
    public List<ClientStatusDTO> getAllClientStatuses() {
        return clientStatusRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get client status by ID
    public ClientStatusDTO getClientStatusById(String id) {
        Optional<ClientStatus> clientStatus = clientStatusRepository.findById(id);
        return clientStatus.map(this::convertToDTO).orElse(null);
    }

    // Update client status
    public void updateClientStatus(String id, ClientStatusDTO clientStatusDTO) throws Exception {
        ClientStatus existingStatus = clientStatusRepository.findById(id).orElse(null);

        if (existingStatus == null) {
            throw new Exception("Client status not found.");
        }

        if (!existingStatus.getStatus().equals(clientStatusDTO.getStatus()) &&
                clientStatusRepository.findByClientStatusName(clientStatusDTO.getStatus()) != null) {
            throw new Exception("A client status with this name already exists.");
        }

        existingStatus.setStatus(clientStatusDTO.getStatus());
        clientStatusRepository.save(existingStatus);
    }

    // Delete client status
    public void deleteClientStatus(String id) {
        clientStatusRepository.deleteById(id);
    }

    // Helper method to convert to DTO
    private ClientStatusDTO convertToDTO(ClientStatus clientStatus) {
        ClientStatusDTO clientStatusDTO = new ClientStatusDTO();
        clientStatusDTO.setClientStatusID(clientStatus.getClientStatusID());
        clientStatusDTO.setStatus(clientStatus.getStatus());
        return clientStatusDTO;
    }
}
