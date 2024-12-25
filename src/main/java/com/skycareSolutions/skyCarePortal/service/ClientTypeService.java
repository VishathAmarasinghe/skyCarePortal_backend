package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.ClientTypeDTO;
import com.skycareSolutions.skyCarePortal.model.ClientType;
import com.skycareSolutions.skyCarePortal.repository.ClientTypeRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientTypeService {

    @Autowired
    private ClientTypeRepository clientTypeRepository;

    // Create a new ClientType
    public void createClientType(ClientTypeDTO clientTypeDTO) throws Exception {
        // Check for duplicates by name
        ClientType clientexisitance = clientTypeRepository.findByClientTypeName(clientTypeDTO.getName());
        if (clientexisitance!=null) {
            throw new Exception("Client Type with the same name already exists.");
        }
        String lastClientTypeID = clientTypeRepository.getLastIdOfClientType();
        String newClientTypeID = Micro_utils.idGenerator("ClientType", lastClientTypeID);
        clientTypeDTO.setClientTypeID(newClientTypeID);
        ClientType clientType = dtoToEntity(clientTypeDTO);

        clientTypeRepository.save(clientType);
    }

    // Get all ClientTypes
    public List<ClientTypeDTO> getAllClientTypes() {
        return clientTypeRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    // Get a ClientType by ID
    public ClientTypeDTO getClientTypeById(Long id) {
        Optional<ClientType> clientType = clientTypeRepository.findById(id.toString());
        return clientType.map(this::entityToDto).orElse(null);
    }

    // Update a ClientType
    public void updateClientType(String id, ClientTypeDTO clientTypeDTO) throws Exception {
        ClientType existingClientType = clientTypeRepository.findById(id).orElse(null);
        if (existingClientType == null) {
            throw new Exception("Client Type not found.");
        }

        existingClientType.setName(clientTypeDTO.getName());
        existingClientType.setStatus(clientTypeDTO.getStatus());
        clientTypeRepository.save(existingClientType);
    }

    // Delete a ClientType by ID
    public void deleteClientType(Long id) {
        clientTypeRepository.deleteById(id.toString());
    }

    // Convert DTO to Entity
    private ClientType dtoToEntity(ClientTypeDTO clientTypeDTO) {
        ClientType clientType = new ClientType();
        clientType.setClientTypeID(clientTypeDTO.getClientTypeID());
        clientType.setName(clientTypeDTO.getName());
        clientType.setStatus(clientTypeDTO.getStatus());
        return clientType;
    }

    // Convert Entity to DTO
    private ClientTypeDTO entityToDto(ClientType clientType) {
        ClientTypeDTO clientTypeDTO = new ClientTypeDTO();
        clientTypeDTO.setClientTypeID(clientType.getClientTypeID());
        clientTypeDTO.setName(clientType.getName());
        clientTypeDTO.setStatus(clientType.getStatus());
        return clientTypeDTO;
    }
}
