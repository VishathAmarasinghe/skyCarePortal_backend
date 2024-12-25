package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.IncidentInvolvedPartiesDTO;
import com.skycareSolutions.skyCarePortal.model.AppointmentAddress;
import com.skycareSolutions.skyCarePortal.model.IncidentInvolvedParties;
import com.skycareSolutions.skyCarePortal.model.Incidents;
import com.skycareSolutions.skyCarePortal.repository.IncidentInvolvedPartiesRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IncidentInvolvedPartiesService {

    @Autowired
    private IncidentInvolvedPartiesRepository partiesRepository;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private AppointmentAddressService addressService;

    private IncidentInvolvedParties toEntity(IncidentInvolvedPartiesDTO dto) {
        IncidentInvolvedParties party = new IncidentInvolvedParties();
        party.setPartyID(dto.getPartyID());
        party.setFirstName(dto.getFirstName());
        party.setLastName(dto.getLastName());
        party.setEmail(dto.getEmail());
        party.setWorkPhoneNo(dto.getWorkPhoneNo());
        party.setOtherPhoneNo(dto.getOtherPhoneNo());
        party.setType(dto.getType());
        party.setIncidents(incidentService.getIncidentByIDWithEntity(dto.getIncidentID()));
        return party;
    }

    private IncidentInvolvedPartiesDTO toDTO(IncidentInvolvedParties party) {
        IncidentInvolvedPartiesDTO dto = new IncidentInvolvedPartiesDTO();
        dto.setPartyID(party.getPartyID());
        dto.setFirstName(party.getFirstName());
        dto.setLastName(party.getLastName());
        dto.setEmail(party.getEmail());
        dto.setWorkPhoneNo(party.getWorkPhoneNo());
        dto.setOtherPhoneNo(party.getOtherPhoneNo());
        dto.setType(party.getType());
        dto.setIncidentID(party.getIncidents().getIncidentID());
        return dto;
    }

    public IncidentInvolvedPartiesDTO createParty(IncidentInvolvedPartiesDTO dto) {
        String lastPartyId = partiesRepository.getLastIdOfIncidentInvolvedParties();
        String newPartyId = Micro_utils.idGenerator("incidentParties", lastPartyId);
        dto.setPartyID(newPartyId);
        IncidentInvolvedParties party = toEntity(dto);
        IncidentInvolvedParties savedParty = partiesRepository.save(party);
        return toDTO(savedParty);
    }

    public ResponseEntity<IncidentInvolvedPartiesDTO> getPartyById(String partyID) {
        IncidentInvolvedParties party = partiesRepository.findById(partyID)
                .orElseThrow(() -> new RuntimeException("Party not found"));
        return ResponseEntity.ok(toDTO(party));
    }

    public ResponseEntity<List<IncidentInvolvedPartiesDTO>> getAllParties() {
        List<IncidentInvolvedPartiesDTO> parties = partiesRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(parties);
    }

    public List<IncidentInvolvedPartiesDTO> getPartiesByIncident(String incidentID) {
        Incidents incident = incidentService.getIncidentByIDWithEntity(incidentID);
        List<IncidentInvolvedParties> parties = partiesRepository.getIncidentInvolvedPartiesByIncidentID(incident);
        List<IncidentInvolvedPartiesDTO> dtos = parties.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return dtos;
    }

    public IncidentInvolvedPartiesDTO updateParty(String partyID, IncidentInvolvedPartiesDTO dto) {
        IncidentInvolvedParties existingParty = partiesRepository.findById(partyID)
                .orElseThrow(() -> new RuntimeException("Party not found"));

        existingParty.setFirstName(dto.getFirstName());
        existingParty.setLastName(dto.getLastName());
        existingParty.setEmail(dto.getEmail());
        existingParty.setWorkPhoneNo(dto.getWorkPhoneNo());
        existingParty.setOtherPhoneNo(dto.getOtherPhoneNo());
        existingParty.setType(dto.getType());
        existingParty.setIncidents(incidentService.getIncidentByIDWithEntity(dto.getIncidentID()));

        IncidentInvolvedParties updatedParty = partiesRepository.save(existingParty);
        return toDTO(updatedParty);
    }

    public ResponseEntity<Void> deleteParty(String partyID) {
        if (!partiesRepository.existsById(partyID)) {
            throw new RuntimeException("Party not found");
        }
        partiesRepository.deleteById(partyID);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> deletePartiesByIncident(String incidentID) {
        Incidents incident = incidentService.getIncidentByIDWithEntity(incidentID);
        List<IncidentInvolvedParties> parties = partiesRepository.getIncidentInvolvedPartiesByIncidentID(incident);

        for (IncidentInvolvedParties party : parties) {
            partiesRepository.deleteById(party.getPartyID());
        }
        return ResponseEntity.noContent().build();
    }
}
