package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.TaskStatusDTO;
import com.skycareSolutions.skyCarePortal.service.TaskStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/task-status")
public class TaskStatusController {

    @Autowired
    private TaskStatusService taskStatusService;

    @GetMapping
    public ResponseEntity<List<TaskStatusDTO>> getAllTaskStatuses() {
        return ResponseEntity.ok(taskStatusService.getAllTaskStatuses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> getTaskStatusById(@PathVariable String id) {
        return ResponseEntity.ok(taskStatusService.getTaskStatusById(id));
    }

    @PostMapping
    public ResponseEntity<TaskStatusDTO> addTaskStatus(@RequestBody TaskStatusDTO taskStatusDTO) {
        return ResponseEntity.ok(taskStatusService.addTaskStatus(taskStatusDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> updateTaskStatus(@PathVariable String id, @RequestBody TaskStatusDTO taskStatusDTO) {
        return ResponseEntity.ok(taskStatusService.updateTaskStatus(id, taskStatusDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskStatus(@PathVariable String id) {
        taskStatusService.deleteTaskStatus(id);
        return ResponseEntity.noContent().build();
    }
}
