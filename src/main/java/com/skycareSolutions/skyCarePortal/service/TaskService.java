package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.AppointmentAddressDTO;
import com.skycareSolutions.skyCarePortal.dto.TaskDTO;
import com.skycareSolutions.skyCarePortal.dto.TaskItemDTO;
import com.skycareSolutions.skyCarePortal.model.AppointmentAddress;
import com.skycareSolutions.skyCarePortal.model.Task;
import com.skycareSolutions.skyCarePortal.repository.TaskRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import com.skycareSolutions.skyCarePortal.utils.TemporaryHiddenClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    @Lazy
    private ClientService clientService;

    @Autowired
    @Lazy
    private CarePlanService carePlanService;

    @Autowired
    @Lazy
    private TaskStatusService taskStatusService;

    @Autowired
    @Lazy
    private AppointmentAddressService appointmentAddressService;

    @Autowired
    @Lazy
    private TaskItemService taskItemService;

    @Autowired
    @Lazy
    private TaskAttachmentService taskAttachmentService;


    @Autowired
    @Lazy
    private TaskImportanceService taskImportanceService;

    // Create a new Task
    public TaskDTO createTask(TaskDTO taskDTO, List<MultipartFile> attachments) {
        AppointmentAddress appointmentAddress =  appointmentAddressService.save(taskDTO.getAppointmentAddress());
        String lastTaskID = taskRepository.getLastIdOfTask();
        String newTaskID = Micro_utils.idGenerator("tasks",lastTaskID);
        taskDTO.setTaskID(newTaskID);
        Task task = mapToEntity(taskDTO);
        Task savedTask = taskRepository.save(task);

        List<TaskItemDTO> taskItems = taskDTO.getTaskItems();
        for (TaskItemDTO taskItemDTO:taskItems){
            taskItemDTO.setTaskID(newTaskID);
        }
        taskItemService.createTaskItems(taskItems);
        taskAttachmentService.createAttachements(attachments,newTaskID);
        return mapToDTO(savedTask);
    }

    // Get all Tasks
    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .filter(task -> task.getClient() == null
                        || !TemporaryHiddenClients.isHidden(task.getClient().getClientID()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get Task by ID
    public TaskDTO getTaskById(String id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            throw new RuntimeException("Task not found with ID: " + id); // Custom Exception can be added
        }
        Task task = taskOptional.get();
        if (task.getClient() != null && TemporaryHiddenClients.isHidden(task.getClient().getClientID())) {
            throw new RuntimeException("Task not found with ID: " + id);
        }
        return mapToDTO(task);
    }

    // Get Task by ID
    public Task getTaskByIdWithEntity(String id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            throw new RuntimeException("Task not found with ID: " + id); // Custom Exception can be added
        }
        return taskOptional.get();
    }

    // Update Task
    public TaskDTO updateTask(String id, TaskDTO taskDTO) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            throw new RuntimeException("Task not found with ID: " + id); // Custom Exception can be added
        }

        Task existingTask = taskOptional.get();
        Task updatedTask = mapToEntity(taskDTO);
        updatedTask.setTaskID(existingTask.getTaskID()); // Ensure the ID remains the same
        Task savedTask = taskRepository.save(updatedTask);
        return mapToDTO(savedTask);
    }

    // Delete Task
    public void deleteTask(String id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with ID: " + id); // Custom Exception can be added
        }
        taskRepository.deleteById(id);
    }

    // Helper method: Convert TaskDTO to Task
    private Task mapToEntity(TaskDTO taskDTO) {
        Task task = new Task();
        task.setTaskID(taskDTO.getTaskID());

        task.setClient(clientService.getClientByIdWithEntitiy(taskDTO.getClientID()));
        task.setCareGiverCount(taskDTO.getCareGiverCount());
        task.setStartDate(taskDTO.getStartDate());
        task.setStartTime(taskDTO.getStartTime());
        task.setEndDate(taskDTO.getEndDate());
        task.setEndTime(taskDTO.getEndTime());
        task.setCarePlan(carePlanService.findById(taskDTO.getCarePlanID()).orElseThrow(null));
        task.setDuration(taskDTO.getDuration());
        task.setComment(taskDTO.getComment());
        task.setBroadcastType(taskDTO.getBroadcastType());
        task.setTaskStatus(taskStatusService.getTaskStatusByIdWithEntitiy(taskDTO.getTaskStatusID()));
        task.setTaskImportance(taskImportanceService.getTaskImportanceByIdWithEntity(taskDTO.getTaskImportanceID()));
        task.setAppointmentAddress(appointmentAddressService.findById(taskDTO.getAppointmentAddress().getAppointmentAddressID()).orElseThrow());
        return task;
    }

    // Helper method: Convert Task to TaskDTO
    private TaskDTO mapToDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskID(task.getTaskID());
        taskDTO.setClientID(task.getClient() != null ? task.getClient().getClientID() : null);
        taskDTO.setCareGiverCount(task.getCareGiverCount());
        taskDTO.setStartDate(task.getStartDate());
        taskDTO.setStartTime(task.getStartTime());
        taskDTO.setEndDate(task.getEndDate());
        taskDTO.setEndTime(task.getEndTime());
        taskDTO.setCarePlanID(task.getCarePlan().getCareplanID());
        taskDTO.setDuration(task.getDuration());
        taskDTO.setComment(task.getComment());
        taskDTO.setBroadcastType(task.getBroadcastType());
        taskDTO.setTaskStatusID(task.getTaskStatus().getStatus());
        taskDTO.setTaskImportanceID(task.getTaskImportance().getName());

        //sync appointment address
        AppointmentAddressDTO appointmentAddressDTO= new AppointmentAddressDTO();
        appointmentAddressDTO.setAppointmentAddressID(task.getAppointmentAddress().getAppointmentAddressID());
        appointmentAddressDTO.setAddress(task.getAppointmentAddress().getAddress());
        appointmentAddressDTO.setCity(task.getAppointmentAddress().getCity());
        appointmentAddressDTO.setState(task.getAppointmentAddress().getState());
        appointmentAddressDTO.setPostalCode(task.getAppointmentAddress().getPostalCode());

        taskDTO.setAppointmentAddress(appointmentAddressDTO);
        taskDTO.setTaskItems(null);
        return taskDTO;
    }
}
