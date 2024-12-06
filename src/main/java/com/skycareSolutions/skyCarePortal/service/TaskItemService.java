package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.TaskItemDTO;
import com.skycareSolutions.skyCarePortal.model.Task;
import com.skycareSolutions.skyCarePortal.model.TaskItems;
import com.skycareSolutions.skyCarePortal.repository.TaskItemRepository;
import com.skycareSolutions.skyCarePortal.repository.TaskRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskItemService {

    @Autowired
    private TaskItemRepository taskItemRepository;

    @Autowired
    private TaskRepository taskRepository;

    // Create Task Items
    public void createTaskItems(List<TaskItemDTO> taskItemDTOs) {
        for (TaskItemDTO task:taskItemDTOs){
            String lastTaskItemID = taskItemRepository.getLastIdOfTaskItems();
            String newTaskItemID = Micro_utils.idGenerator("taskItems",lastTaskItemID);
            task.setItemID(newTaskItemID);
            taskItemRepository.save(mapToEntity(task));
        }
    }

    // Get All Task Items
    public List<TaskItemDTO> getAllTaskItems() {
        return taskItemRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get Task Item by ID
    public TaskItemDTO getTaskItemById(String id) {
        Optional<TaskItems> taskItem = taskItemRepository.findById(id);
        if (taskItem.isEmpty()) {
            throw new RuntimeException("Task Item not found with ID: " + id); // Replace with a custom exception
        }
        return mapToDTO(taskItem.get());
    }

    // Update Task Item
    public TaskItemDTO updateTaskItem(String id, TaskItemDTO taskItemDTO) {
        Optional<TaskItems> existingTaskItem = taskItemRepository.findById(id);
        if (existingTaskItem.isEmpty()) {
            throw new RuntimeException("Task Item not found with ID: " + id); // Replace with a custom exception
        }
        TaskItems updatedTaskItem = mapToEntity(taskItemDTO);
        updatedTaskItem.setItemID(id); // Ensure ID remains unchanged
        TaskItems savedTaskItem = taskItemRepository.save(updatedTaskItem);
        return mapToDTO(savedTaskItem);
    }

    // Delete Task Item
    public void deleteTaskItem(String id) {
        if (!taskItemRepository.existsById(id)) {
            throw new RuntimeException("Task Item not found with ID: " + id); // Replace with a custom exception
        }
        taskItemRepository.deleteById(id);
    }

    // Helper method: Convert TaskItemDTO to TaskItems
    private TaskItems mapToEntity(TaskItemDTO taskItemDTO) {
        TaskItems taskItem = new TaskItems();
        taskItem.setItemID(taskItemDTO.getItemID());
        Task task = taskRepository.findById(taskItemDTO.getTaskID())
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskItemDTO.getTaskID()));
        taskItem.setTaskID(task);
        taskItem.setTitle(taskItemDTO.getTitle());
        taskItem.setDescription(taskItemDTO.getDescription());
        return taskItem;
    }

    // Helper method: Convert TaskItems to TaskItemDTO
    private TaskItemDTO mapToDTO(TaskItems taskItem) {
        TaskItemDTO taskItemDTO = new TaskItemDTO();
        taskItemDTO.setItemID(taskItem.getItemID());
        taskItemDTO.setTaskID(taskItem.getTaskID().getTaskID());
        taskItemDTO.setTitle(taskItem.getTitle());
        taskItemDTO.setDescription(taskItem.getDescription());
        return taskItemDTO;
    }
}
