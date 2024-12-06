package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.TaskImportanceDTO;
import com.skycareSolutions.skyCarePortal.service.TaskImportanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/task-importance")
public class TaskImportanceController {

    @Autowired
    private TaskImportanceService taskImportanceService;

    @GetMapping
    public ResponseEntity<List<TaskImportanceDTO>> getAllTaskImportances() {
        return ResponseEntity.ok(taskImportanceService.getAllTaskImportances());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskImportanceDTO> getTaskImportanceById(@PathVariable String id) {
        return ResponseEntity.ok(taskImportanceService.getTaskImportanceById(id));
    }

    @PostMapping
    public ResponseEntity<TaskImportanceDTO> addTaskImportance(@RequestBody TaskImportanceDTO taskImportanceDTO) {
        return ResponseEntity.ok(taskImportanceService.addTaskImportance(taskImportanceDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskImportanceDTO> updateTaskImportance(@PathVariable String id, @RequestBody TaskImportanceDTO taskImportanceDTO) {
        return ResponseEntity.ok(taskImportanceService.updateTaskImportance(id, taskImportanceDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskImportance(@PathVariable String id) {
        taskImportanceService.deleteTaskImportance(id);
        return ResponseEntity.noContent().build();
    }
}
