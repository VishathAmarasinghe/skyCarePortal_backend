package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skycareSolutions.skyCarePortal.dto.TaskDTO;
import com.skycareSolutions.skyCarePortal.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/tasks")
public class TaskController {

    private final ObjectMapper objectMapper;

    public TaskController() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Register the JSR310 module
    }

    @Autowired
    private TaskService taskService;

    // Create a new Task
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestPart(name = "task") String taskData, @RequestPart(name = "attachments")List<MultipartFile> attachments) {
        try {
            TaskDTO taskDTO = objectMapper.readValue(taskData, TaskDTO.class);
            TaskDTO createdTask = taskService.createTask(taskDTO,attachments);
            return ResponseEntity.ok(createdTask);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // Get all Tasks
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    // Get Task by ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable String id) {
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    // Update Task
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable String id, @RequestBody TaskDTO taskDTO) {
        TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(updatedTask);
    }

    // Delete Task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
