package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.TaskItemDTO;
import com.skycareSolutions.skyCarePortal.service.TaskItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/task-items")
public class TaskItemController {

    @Autowired
    private TaskItemService taskItemService;

    // Create Task Items
    @PostMapping
    public ResponseEntity<ApiResponse> createTaskItems(@RequestBody List<TaskItemDTO> taskItemDTOs) {
        taskItemService.createTaskItems(taskItemDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Task Items Created successfully!",true));
    }

    // Get All Task Items
    @GetMapping
    public ResponseEntity<List<TaskItemDTO>> getAllTaskItems() {
        List<TaskItemDTO> taskItems = taskItemService.getAllTaskItems();
        return ResponseEntity.ok(taskItems);
    }

    // Get Task Item by ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskItemDTO> getTaskItemById(@PathVariable String id) {
        TaskItemDTO taskItem = taskItemService.getTaskItemById(id);
        return ResponseEntity.ok(taskItem);
    }

    // Update Task Item
    @PutMapping("/{id}")
    public ResponseEntity<TaskItemDTO> updateTaskItem(@PathVariable String id, @RequestBody TaskItemDTO taskItemDTO) {
        TaskItemDTO updatedTaskItem = taskItemService.updateTaskItem(id, taskItemDTO);
        return ResponseEntity.ok(updatedTaskItem);
    }

    // Delete Task Item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskItem(@PathVariable String id) {
        taskItemService.deleteTaskItem(id);
        return ResponseEntity.noContent().build();
    }
}
