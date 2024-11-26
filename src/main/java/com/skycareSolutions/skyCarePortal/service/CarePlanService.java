package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CarePlanBilablesDTO;
import com.skycareSolutions.skyCarePortal.dto.CarePlanDTO;
import com.skycareSolutions.skyCarePortal.dto.CarePlanLongTermGoalDTO;
import com.skycareSolutions.skyCarePortal.dto.CarePlanShortTermGoalDTO;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.*;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarePlanService {

    @Autowired
    private CarePlanRepository carePlanRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CarePlanLongTermRepository carePlanLongTermRepository;

    @Autowired
    private CarePlanBillableRepository carePlanBillableRepository;

    @Autowired
    private CarePlanShortTermRepository carePlanShortTermRepository;

    @Autowired
    private CarePlanStatusRepository carePlanStatusRepository;


    @Autowired
    private CarePlanLongTermGoalService carePlanLongTermGoalService;

    @Autowired
    private CarePlanShortTermGoalService carePlanShortTermGoalService;

    @Autowired
    private CarePlanBillablesService carePlanBillablesService;

    // DTO to Entity Conversion
    private CarePlan toEntity(CarePlanDTO carePlanDTO) {
        CarePlan carePlan = new CarePlan();
        carePlan.setTitle(carePlanDTO.getTitle());
        carePlan.setCareplanID(carePlanDTO.getCareplanID());
        carePlan.setStartDate(carePlanDTO.getStartDate());
        carePlan.setEndDate(carePlanDTO.getEndDate());
        return carePlan;
    }

    // Entity to DTO Conversion
    private CarePlanDTO toDTO(CarePlan carePlan) {
        CarePlanDTO carePlanDTO = new CarePlanDTO();
        carePlanDTO.setTitle(carePlan.getTitle());
        carePlanDTO.setCareplanID(carePlan.getCareplanID());
        carePlanDTO.setStartDate(carePlan.getStartDate());
        carePlanDTO.setEndDate(carePlan.getEndDate());
        carePlanDTO.setClientID(carePlan.getClientID().getClientID());
        carePlanDTO.setCarePlanStatusID(carePlan.getCarePlanStatus().getCareplanStatusID());


        List<CarePlanBilablesDTO> carePlanBilllist = carePlanBillablesService.getBillsByCarePlanID(carePlan);
        carePlanDTO.setCarePlanBillables(carePlanBilllist);

        List<CarePlanLongTermGoalDTO> longTermList = carePlanLongTermGoalService.findByCarePlan(carePlan);
        for (CarePlanLongTermGoalDTO longGoals:longTermList){
            List<CarePlanShortTermGoalDTO> shortTermGoal = carePlanShortTermGoalService.findByLongTermGoal(longGoals.getLongGoalID());
            longGoals.setCarePlanShortTermGoals(shortTermGoal);
        }
        carePlanDTO.setCarePlanLongTermGoals(longTermList);
        return carePlanDTO;
    }

    // Get CarePlans by clientID
    public List<CarePlanDTO> getCarePlansByClientID(String clientID) {
        System.out.println("client id " + clientID);
        Client client = clientRepository.findById(clientID)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientID));

        // Now, passing the full Client object instead of just the clientID
        return carePlanRepository.findByClient(client).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get all CarePlans
    public List<CarePlanDTO> getAllCarePlans() {
        return carePlanRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Create a new CarePlan
    public CarePlanDTO createCarePlan(CarePlanDTO carePlanDTO) {
        CarePlan carePlan = toEntity(carePlanDTO);
        String lastCarePlanID = carePlanRepository.getLastIdOfCarePlan();
        String newCarePlanID = Micro_utils.idGenerator("careplan",lastCarePlanID);
        carePlan.setCareplanID(newCarePlanID);

        Client clientValue = clientRepository.findById(carePlanDTO.getClientID())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + carePlanDTO.getClientID()));
        carePlan.setClientID(clientValue);

        CarePlanStatus carePlanStatusValue = carePlanStatusRepository.findById(carePlanDTO.getCarePlanStatusID())
                .orElseThrow(() -> new RuntimeException("Care Plan Status not found with ID: " + carePlanDTO.getCarePlanStatusID()));
        carePlan.setCarePlanStatus(carePlanStatusValue);

        carePlanRepository.save(carePlan);
        List<CarePlanLongTermGoalDTO>  longTermPlans = carePlanDTO.getCarePlanLongTermGoals();
        for (CarePlanLongTermGoalDTO lPlan:longTermPlans){
            String lastLongTermCarePlanID = carePlanLongTermRepository.getLastIdOfLongTermCarePlan();
            String newLongTermCarePlanID = Micro_utils.idGenerator("longTermCarePlan",lastLongTermCarePlanID);
            List<CarePlanShortTermGoalDTO> shortTermPlan = lPlan.getCarePlanShortTermGoals();
            lPlan.setCareplanID(newCarePlanID);
            lPlan.setLongGoalID(newLongTermCarePlanID);
            carePlanLongTermGoalService.saveOrUpdate(lPlan);
            for(CarePlanShortTermGoalDTO splan:shortTermPlan ){
                String lastShortTermCarePlanID = carePlanShortTermRepository.getLastIdOfShortTermCarePlan();
                String newShortTermCarePlanID = Micro_utils.idGenerator("shortTermCarePlan",lastShortTermCarePlanID);
                splan.setCareplanID(newCarePlanID);
                System.out.println(" care plan short term id "+newCarePlanID);
                splan.setLongTermGoalID(newLongTermCarePlanID);
                splan.setShortTermGoalID(newShortTermCarePlanID);
                carePlanShortTermGoalService.saveOrUpdate(splan);
            }
        }
        List<CarePlanBilablesDTO> billList = carePlanDTO.getCarePlanBillables();
        for (CarePlanBilablesDTO bill:billList){
            bill.setCareplanID(newCarePlanID);
            carePlanBillablesService.createCarePlanBill(bill);

        }
        return toDTO(carePlan);
    }

    // get careplan by id
    public Optional<CarePlan> findById(String id) {
        return carePlanRepository.findById(id);
    }

    // Update a CarePlan
    public CarePlanDTO updateCarePlan(String id, CarePlanDTO carePlanDTO) {
        CarePlan carePlan = carePlanRepository.findById(id).orElseThrow(() -> new RuntimeException("CarePlan not found"));
        carePlan.setStartDate(carePlanDTO.getStartDate());
        carePlan.setEndDate(carePlanDTO.getEndDate());
        carePlanRepository.save(carePlan);
        return toDTO(carePlan);
    }

    // Delete a CarePlan
    public void deleteCarePlan(String id) {
        carePlanRepository.deleteById(id);
    }
}
