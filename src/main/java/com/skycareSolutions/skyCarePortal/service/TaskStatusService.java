package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.TaskStatusDTO;
import com.skycareSolutions.skyCarePortal.model.TaskStatus;
import com.skycareSolutions.skyCarePortal.repository.TaskStatusRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    public List<TaskStatusDTO> getAllTaskStatuses() {
        return taskStatusRepository.findAll().stream()
                .map(taskStatus -> new TaskStatusDTO(taskStatus.getTaskStatusID(), taskStatus.getStatus()))
                .collect(Collectors.toList());
    }

    public TaskStatusDTO getTaskStatusById(String id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElseThrow(() -> new RuntimeException("Task Status not found"));
        return new TaskStatusDTO(taskStatus.getTaskStatusID(), taskStatus.getStatus());
    }

    public TaskStatus getTaskStatusByIdWithEntitiy(String id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElseThrow(() -> new RuntimeException("Task Status not found"));
        return taskStatus;
    }

    public TaskStatusDTO addTaskStatus(TaskStatusDTO taskStatusDTO) {
        String lastTaskStatusID = taskStatusRepository.getLastIdOfTaskStatus();
        String newTaskStatusID = Micro_utils.idGenerator("taskStatus",lastTaskStatusID);

        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setTaskStatusID(newTaskStatusID);
        taskStatus.setStatus(taskStatusDTO.getStatus());
        taskStatus = taskStatusRepository.save(taskStatus);
        return new TaskStatusDTO(taskStatus.getTaskStatusID(), taskStatus.getStatus());
    }

    public TaskStatusDTO updateTaskStatus(String id, TaskStatusDTO taskStatusDTO) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElseThrow(() -> new RuntimeException("Task Status not found"));
        taskStatus.setStatus(taskStatusDTO.getStatus());
        taskStatus = taskStatusRepository.save(taskStatus);
        return new TaskStatusDTO(taskStatus.getTaskStatusID(), taskStatus.getStatus());
    }

    public void deleteTaskStatus(String id) {
        if (!taskStatusRepository.existsById(id)) {
            throw new RuntimeException("Task Status not found");
        }
        taskStatusRepository.deleteById(id);
    }
}
