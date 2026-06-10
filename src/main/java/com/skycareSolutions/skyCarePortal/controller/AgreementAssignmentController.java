package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.AgreementTemplateAssignmentDTO;
import com.skycareSolutions.skyCarePortal.service.AgreementAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/agreement-template-assignments")
public class AgreementAssignmentController {

    @Autowired
    private AgreementAssignmentService assignmentService;

    @GetMapping
    public ResponseEntity<List<AgreementTemplateAssignmentDTO>> getAll() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    @PutMapping
    public ResponseEntity<List<AgreementTemplateAssignmentDTO>> replaceAll(
            @RequestBody List<AgreementTemplateAssignmentDTO> assignments
    ) {
        return ResponseEntity.ok(assignmentService.replaceAssignments(assignments));
    }
}
