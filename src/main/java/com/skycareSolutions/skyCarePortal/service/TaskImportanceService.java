package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.TaskImportanceDTO;
import com.skycareSolutions.skyCarePortal.model.TaskImportance;
import com.skycareSolutions.skyCarePortal.repository.TaskImportanceRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskImportanceService {

    @Autowired
    private TaskImportanceRepository taskImportanceRepository;

    public List<TaskImportanceDTO> getAllTaskImportances() {
        return taskImportanceRepository.findAll().stream()
                .map(taskImportance -> new TaskImportanceDTO(taskImportance.getTaskImportanceID(), taskImportance.getName()))
                .collect(Collectors.toList());
    }

    public TaskImportanceDTO getTaskImportanceById(String id) {
        TaskImportance taskImportance = taskImportanceRepository.findById(id).orElseThrow(() -> new RuntimeException("Task Importance not found"));
        return new TaskImportanceDTO(taskImportance.getTaskImportanceID(), taskImportance.getName());
    }

    public TaskImportance getTaskImportanceByIdWithEntity(String id) {
        TaskImportance taskImportance = taskImportanceRepository.findById(id).orElseThrow(() -> new RuntimeException("Task Importance not found"));
        return taskImportance;
    }

    public TaskImportanceDTO addTaskImportance(TaskImportanceDTO taskImportanceDTO) {
        String lastTaskImportanceID = taskImportanceRepository.getLastIdOfTaskImportance();
        String newTaskImportanceID = Micro_utils.idGenerator("taskImportance",lastTaskImportanceID);

        TaskImportance taskImportance = new TaskImportance();
        taskImportance.setTaskImportanceID(newTaskImportanceID);
        taskImportance.setName(taskImportanceDTO.getName());
        taskImportance = taskImportanceRepository.save(taskImportance);
        return new TaskImportanceDTO(taskImportance.getTaskImportanceID(), taskImportance.getName());
    }

    public TaskImportanceDTO updateTaskImportance(String id, TaskImportanceDTO taskImportanceDTO) {
        TaskImportance taskImportance = taskImportanceRepository.findById(id).orElseThrow(() -> new RuntimeException("Task Importance not found"));
        taskImportance.setName(taskImportanceDTO.getName());
        taskImportance = taskImportanceRepository.save(taskImportance);
        return new TaskImportanceDTO(taskImportance.getTaskImportanceID(), taskImportance.getName());
    }

    public void deleteTaskImportance(String id) {
        if (!taskImportanceRepository.existsById(id)) {
            throw new RuntimeException("Task Importance not found");
        }
        taskImportanceRepository.deleteById(id);
    }
}
