package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.IncidentActionTypeAllAnswersDTO;
import com.skycareSolutions.skyCarePortal.service.IncidentActionTypeAllAnswersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/incident-answers")
public class IncidentActionTypeAllAnswersController {

    @Autowired
    private IncidentActionTypeAllAnswersService service;

    // Create a new answer
    @PostMapping
    public ResponseEntity<IncidentActionTypeAllAnswersDTO> createAnswer(@RequestBody IncidentActionTypeAllAnswersDTO answerDTO) {
        IncidentActionTypeAllAnswersDTO savedAnswer = service.saveAnswer(answerDTO);
        return ResponseEntity.ok(savedAnswer);
    }

    // Get all answers
    @GetMapping
    public ResponseEntity<List<IncidentActionTypeAllAnswersDTO>> getAllAnswers() {
        List<IncidentActionTypeAllAnswersDTO> answers = service.getAllAnswers();
        return ResponseEntity.ok(answers);
    }

    // Get an answer by ID
    @GetMapping("/{id}")
    public ResponseEntity<IncidentActionTypeAllAnswersDTO> getAnswerById(@PathVariable Long id) {
        Optional<IncidentActionTypeAllAnswersDTO> answer = service.getAnswerById(id);
        return answer.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Get answers by IncidentActionType ID
    @GetMapping("/action/{incidentActionID}")
    public ResponseEntity<List<IncidentActionTypeAllAnswersDTO>> getAnswersByAction(@PathVariable String incidentActionID) {
        List<IncidentActionTypeAllAnswersDTO> answers = service.getAnswersByAction(incidentActionID);
        return ResponseEntity.ok(answers);
    }

    // Get answers by IncidentSubActionType ID
    @GetMapping("/sub-action/{incidentSubActionID}")
    public ResponseEntity<List<IncidentActionTypeAllAnswersDTO>> getAnswersBySubAction(@PathVariable Long incidentSubActionID) {
        List<IncidentActionTypeAllAnswersDTO> answers = service.getAnswersBySubAction(incidentSubActionID);
        return ResponseEntity.ok(answers);
    }

    // Get answers by Incident ID
    @GetMapping("/incident/{incidentID}")
    public ResponseEntity<List<IncidentActionTypeAllAnswersDTO>> getAnswersByIncident(@PathVariable String incidentID) {
        List<IncidentActionTypeAllAnswersDTO> answers = service.getAnswersByIncident(incidentID);
        return ResponseEntity.ok(answers);
    }

    // Delete an answer by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        service.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }
}
