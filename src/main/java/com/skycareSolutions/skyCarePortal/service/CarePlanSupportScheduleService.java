package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CarePlanSupportScheduleDTO;
import com.skycareSolutions.skyCarePortal.model.CarePlan;
import com.skycareSolutions.skyCarePortal.model.CarePlanSupportSchedule;
import com.skycareSolutions.skyCarePortal.repository.CarePlanRepository;
import com.skycareSolutions.skyCarePortal.repository.CarePlanSupportScheduleRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarePlanSupportScheduleService {

    @Autowired
    private CarePlanSupportScheduleRepository carePlanSupportScheduleRepository;

    @Autowired
    private CarePlanRepository carePlanRepository;

    public List<CarePlanSupportScheduleDTO> getSchedulesByCarePlan(CarePlan carePlan) {
        return carePlanSupportScheduleRepository.findAllByCarePlan(carePlan)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CarePlanSupportScheduleDTO createSchedule(CarePlanSupportScheduleDTO dto) {
        String lastId = carePlanSupportScheduleRepository.getLastIdOfCarePlanSupportSchedule();
        String newId = Micro_utils.idGenerator("carePlanSupportSchedule", lastId);
        dto.setScheduleID(newId);
        CarePlanSupportSchedule entity = mapToEntity(dto);
        entity = carePlanSupportScheduleRepository.save(entity);
        return mapToDTO(entity);
    }

    public CarePlanSupportScheduleDTO updateSchedule(String id, CarePlanSupportScheduleDTO dto) {
        CarePlanSupportSchedule entity = carePlanSupportScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Support schedule not found with ID: " + id));

        CarePlan careplanValue = carePlanRepository.findById(dto.getCareplanID())
                .orElseThrow(() -> new RuntimeException("CarePlan not found with ID: " + dto.getCareplanID()));

        entity.setCareplanID(careplanValue);
        entity.setDay(dto.getDay());
        entity.setSupportType(dto.getSupportType());
        entity.setHours(dto.getHours());
        entity.setRate(dto.getRate());

        entity = carePlanSupportScheduleRepository.save(entity);
        return mapToDTO(entity);
    }

    private CarePlanSupportScheduleDTO mapToDTO(CarePlanSupportSchedule entity) {
        CarePlanSupportScheduleDTO dto = new CarePlanSupportScheduleDTO();
        dto.setScheduleID(entity.getScheduleID());
        dto.setCareplanID(entity.getCareplanID().getCareplanID());
        dto.setDay(entity.getDay());
        dto.setSupportType(entity.getSupportType());
        dto.setHours(entity.getHours());
        dto.setRate(entity.getRate());
        return dto;
    }

    private CarePlanSupportSchedule mapToEntity(CarePlanSupportScheduleDTO dto) {
        CarePlan careplanValue = carePlanRepository.findById(dto.getCareplanID())
                .orElseThrow(() -> new RuntimeException("CarePlan not found with ID: " + dto.getCareplanID()));
        return new CarePlanSupportSchedule(
                dto.getScheduleID(),
                careplanValue,
                dto.getDay(),
                dto.getSupportType(),
                dto.getHours(),
                dto.getRate()
        );
    }
}

