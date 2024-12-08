package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.RecurrentWorkDTO;
import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.model.RecurrentWork;
import com.skycareSolutions.skyCarePortal.repository.RecurrentWorkRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecurrentWorkService {

    @Autowired
    private RecurrentWorkRepository recurrentWorkRepository;

    @Autowired
    @Lazy
    private AppointmentService appointmentService;

    // Map DTO to Entity
    private RecurrentWork mapToEntity(RecurrentWorkDTO dto) {
        RecurrentWork entity = new RecurrentWork();
        entity.setRecurrentWorkID(dto.getRecurrentWorkID());
        entity.setRecurrenceType(dto.getRecurrenceType());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setFrequencyCount(dto.getFrequencyCount());
        entity.setDay(dto.getDay());
        entity.setOccurrenceLimit(dto.getOccurrenceLimit());

        if (dto.getAppointmentID()!=null && dto.getAppointmentID()!=""){
            entity.setAppointment(appointmentService.getAppointmentByIdWithEntity(dto.getAppointmentID()));
        }

        // Note: Map appointment and task as needed
        return entity;
    }

    // Map Entity to DTO
    private RecurrentWorkDTO mapToDTO(RecurrentWork entity) {
        RecurrentWorkDTO dto = new RecurrentWorkDTO();
        dto.setRecurrentWorkID(entity.getRecurrentWorkID());
        dto.setRecurrenceType(entity.getRecurrenceType());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setFrequencyCount(entity.getFrequencyCount());
        dto.setDay(entity.getDay());
        dto.setOccurrenceLimit(entity.getOccurrenceLimit());
        if (entity.getTask() != null && entity.getTask().getTaskID() != null) {
            dto.setTaskID(entity.getTask().getTaskID());
        }

        if (entity.getAppointment() != null && entity.getAppointment().getAppointmentID() != null) {
            dto.setAppointmentID(entity.getAppointment().getAppointmentID());
        }

        return dto;
    }

    // create new recurrent work
    public RecurrentWorkDTO createRecurrentWork(RecurrentWorkDTO dto) {
        String lastRecurrentWorkID = recurrentWorkRepository.getLastIdOfRecurrentTask();
        String newRecurrnetWorkID = Micro_utils.idGenerator("recurrentWork",lastRecurrentWorkID);
        dto.setRecurrentWorkID(newRecurrnetWorkID);
        RecurrentWork entity = mapToEntity(dto);
        RecurrentWork saved = recurrentWorkRepository.save(entity);
        return mapToDTO(saved);
    }

    public List<RecurrentWorkDTO> getAllRecurrentWorks() {
        return ((List<RecurrentWork>) recurrentWorkRepository.findAll())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public RecurrentWorkDTO getRecurrentWorkTaskByAppointment(String appointmentID){
        Appointment appointment = appointmentService.getAppointmentByIdWithEntity(appointmentID);
        if (appointment!=null){
            RecurrentWork work = recurrentWorkRepository.getRecurrentWorkTaskByAppointmentID(appointment);
            return mapToDTO(work);
        }else{
            throw new RuntimeException();
        }
    }

    public Optional<RecurrentWorkDTO> getRecurrentWorkById(String id) {
        return recurrentWorkRepository.findById(id).map(this::mapToDTO);
    }

    public Optional<RecurrentWorkDTO> updateRecurrentWork(String id, RecurrentWorkDTO dto) {
        return recurrentWorkRepository.findById(id).map(existing -> {
            existing.setRecurrenceType(dto.getRecurrenceType());
            existing.setStartDate(dto.getStartDate());
            existing.setEndDate(dto.getEndDate());
            existing.setFrequencyCount(dto.getFrequencyCount());
            existing.setDay(dto.getDay());
            existing.setOccurrenceLimit(dto.getOccurrenceLimit());
            // Note: Update appointment and task as needed
            RecurrentWork updated = recurrentWorkRepository.save(existing);
            return mapToDTO(updated);
        });
    }

    public RecurrentWork getRecurrentWorkByIDWithEntity(String recurrentWorkID){
        Optional<RecurrentWork> work = recurrentWorkRepository.findById(recurrentWorkID);
        if (work.isPresent()){
            return work.get();
        }else{
            return new RecurrentWork();
        }
    }

    public boolean deleteRecurrentWork(String id) {
        if (recurrentWorkRepository.existsById(id)) {
            recurrentWorkRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
