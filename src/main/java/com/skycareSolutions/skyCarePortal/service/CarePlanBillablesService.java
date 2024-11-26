package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CarePlanBilablesDTO;
import com.skycareSolutions.skyCarePortal.model.CarePlan;
import com.skycareSolutions.skyCarePortal.model.CarePlanBilables;
import com.skycareSolutions.skyCarePortal.model.GoalOutcome;
import com.skycareSolutions.skyCarePortal.repository.CarePlanBillableRepository;
import com.skycareSolutions.skyCarePortal.repository.CarePlanRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarePlanBillablesService {

    @Autowired
    private CarePlanBillableRepository carePlanBillableRepository;

    @Autowired
    private CarePlanRepository carePlanRepository;


    public List<CarePlanBilablesDTO> getBillsByCarePlanID(CarePlan carePlan) {
        return carePlanBillableRepository.findAllBillablesByCarePlanID(carePlan)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CarePlanBilablesDTO getBillById(String id) {
        CarePlanBilables bill = carePlanBillableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with ID: " + id));
        return mapToDTO(bill);
    }

    public CarePlanBilablesDTO createCarePlanBill(CarePlanBilablesDTO billDTO) {
        String lastCarePlanBillId = carePlanBillableRepository.getLastIdOfCarePlanBillables();
        String newCarePlanBillID = Micro_utils.idGenerator("carePlanBillables",lastCarePlanBillId);
        billDTO.setBillID(newCarePlanBillID);
        CarePlanBilables bill = mapToEntity(billDTO);
        bill = carePlanBillableRepository.save(bill);
        return mapToDTO(bill);
    }

    public CarePlanBilablesDTO updateCarePlanBill(String id, CarePlanBilablesDTO billDTO) {
        CarePlanBilables bill = carePlanBillableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with ID: " + id));

        CarePlan careplanValue = carePlanRepository.findById(billDTO.getCareplanID())
                .orElseThrow(() -> new RuntimeException("CarePlan not found with ID: " + billDTO.getCareplanID()));

        bill.setName(billDTO.getName());
        bill.setAmount(billDTO.getAmount());
        bill.setCareplanID(careplanValue);

        bill = carePlanBillableRepository.save(bill);
        return mapToDTO(bill);
    }

    public ResponseEntity<Void> deleteCarePlanBill(String id) {
        if (!carePlanBillableRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        carePlanBillableRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private CarePlanBilablesDTO mapToDTO(CarePlanBilables bill) {
        CarePlanBilablesDTO dto = new CarePlanBilablesDTO();
        dto.setBillID(bill.getBillID());
        dto.setName(bill.getName());
        dto.setAmount(bill.getAmount());
        dto.setCareplanID(bill.getCareplanID().getCareplanID());
        return dto;
    }

    private CarePlanBilables mapToEntity(CarePlanBilablesDTO dto) {
        CarePlan careplanValue = carePlanRepository.findById(dto.getCareplanID())
                .orElseThrow(() -> new RuntimeException("CarePlan not found with ID: " + dto.getCareplanID()));
        return new CarePlanBilables(
                dto.getBillID(),
                dto.getName(),
                dto.getAmount(),
                careplanValue

        );
    }
}
