package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.CreateIncidentQuestionsDTO;
import com.skycareSolutions.skyCarePortal.dto.IncidentActionTypesDTO;
import com.skycareSolutions.skyCarePortal.dto.IncidentSubActionTypesDTO;
import com.skycareSolutions.skyCarePortal.service.IncidentActionTypesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/incident-action-types")
public class IncidentActionTypesController {

    @Autowired
    private IncidentActionTypesService incidentActionTypesService;

    // Fetch all Incident Action Types
    @GetMapping
    public ResponseEntity<List<IncidentActionTypesDTO>> getAllActionTypes() {
        List<IncidentActionTypesDTO> actionTypes = incidentActionTypesService.getAllActionTypes();
        return ResponseEntity.ok(actionTypes);
    }

    // Fetch a single Incident Action Type by ID
    @GetMapping("/{id}")
    public ResponseEntity<IncidentActionTypesDTO> getActionTypeById(@PathVariable String id) {
        IncidentActionTypesDTO actionType = incidentActionTypesService.getActionTypeById(id);
        return ResponseEntity.ok(actionType);
    }

    // Create a new Incident Action Type
    @PostMapping
    public ResponseEntity<IncidentActionTypesDTO> createActionType(@RequestBody IncidentActionTypesDTO dto) {
        IncidentActionTypesDTO createdActionType = incidentActionTypesService.createActionType(dto);
        return ResponseEntity.ok(createdActionType);
    }

    // Create Incident Action Types with Sub Questions
    @PostMapping("/with-sub-questions")
    public ResponseEntity<Boolean> createActionTypeWithSubQuestions(@RequestBody List<CreateIncidentQuestionsDTO> questionsDTOList) {
        Boolean result = incidentActionTypesService.createActionTypeWithSubQuestions(questionsDTOList);
        return ResponseEntity.ok(result);
    }

    // Update an existing Incident Action Type
    @PutMapping("/{id}")
    public ResponseEntity<IncidentActionTypesDTO> updateActionType(@PathVariable String id, @RequestBody IncidentActionTypesDTO dto) {
        IncidentActionTypesDTO updatedActionType = incidentActionTypesService.updateActionType(id, dto);
        return ResponseEntity.ok(updatedActionType);
    }

    // Delete an Incident Action Type
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActionType(@PathVariable String id) {
        incidentActionTypesService.deleteActionType(id);
        return ResponseEntity.noContent().build();
    }

    // Fetch all Sub Action Types for a given Action Type ID
    @GetMapping("/{id}/sub-actions")
    public ResponseEntity<List<IncidentSubActionTypesDTO>> getAllSubActionTypes(@PathVariable String id) {
        List<IncidentSubActionTypesDTO> subActionTypes = incidentActionTypesService.getAllSubActionTypes(id);
        return ResponseEntity.ok(subActionTypes);
    }

    // Create a Sub Action Type
    @PostMapping("/{id}/sub-actions")
    public ResponseEntity<IncidentSubActionTypesDTO> createSubActionType(
            @PathVariable String id,
            @RequestBody IncidentSubActionTypesDTO subActionDto) {
        IncidentSubActionTypesDTO createdSubAction = incidentActionTypesService.createSubActionType(id, subActionDto);
        return ResponseEntity.ok(createdSubAction);
    }

    // Update a Sub Action Type
    @PutMapping("/sub-actions/{subActionId}")
    public ResponseEntity<IncidentSubActionTypesDTO> updateSubActionType(
            @PathVariable Long subActionId,
            @RequestBody IncidentSubActionTypesDTO subActionDto) {
        IncidentSubActionTypesDTO updatedSubAction = incidentActionTypesService.updateSubActionType(subActionId, subActionDto);
        return ResponseEntity.ok(updatedSubAction);
    }

    // Delete a Sub Action Type
    @DeleteMapping("/sub-actions/{subActionId}")
    public ResponseEntity<Void> deleteSubActionType(@PathVariable Long subActionId) {
        incidentActionTypesService.deleteSubActionType(subActionId);
        return ResponseEntity.noContent().build();
    }
}
