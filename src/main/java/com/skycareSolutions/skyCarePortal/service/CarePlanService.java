package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CarePlanBilablesDTO;
import com.skycareSolutions.skyCarePortal.dto.CarePlanDTO;
import com.skycareSolutions.skyCarePortal.dto.CarePlanLongTermGoalDTO;
import com.skycareSolutions.skyCarePortal.dto.CarePlanShortTermGoalDTO;
import com.skycareSolutions.skyCarePortal.dto.CarePlanSupportScheduleDTO;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.*;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import com.skycareSolutions.skyCarePortal.utils.TemporaryHiddenClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
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

    @Autowired
    private CarePlanSupportScheduleService carePlanSupportScheduleService;

    @Autowired
    private CarePlanSupportScheduleRepository carePlanSupportScheduleRepository;

    // DTO to Entity Conversion
    private CarePlan toEntity(CarePlanDTO carePlanDTO) {
        CarePlan carePlan = new CarePlan();
        carePlan.setTitle(carePlanDTO.getTitle());
        carePlan.setCareplanID(carePlanDTO.getCareplanID());
        carePlan.setStartDate(carePlanDTO.getStartDate());
        carePlan.setEndDate(carePlanDTO.getEndDate());
        carePlan.setStatus(carePlanDTO.getStatus());
        carePlan.setCurrentLivingSituation(carePlanDTO.getCurrentLivingSituation());
        carePlan.setSupportsFundedCore(carePlanDTO.getSupportsFundedCore());
        carePlan.setSupportsFundedCapacityBuilding(carePlanDTO.getSupportsFundedCapacityBuilding());
        carePlan.setSupportsFundedCapital(carePlanDTO.getSupportsFundedCapital());
        carePlan.setRiskManagement(carePlanDTO.getRiskManagement());
        carePlan.setExpectedOutcomes(carePlanDTO.getExpectedOutcomes());
        carePlan.setBillingBudgetNotes(carePlanDTO.getBillingBudgetNotes());
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
        carePlanDTO.setCarePlanStatusID(
                carePlan.getCarePlanStatus() != null
                        ? carePlan.getCarePlanStatus().getCareplanStatusID()
                        : null
        );
        carePlanDTO.setStatus(carePlan.getStatus());

        carePlanDTO.setCurrentLivingSituation(carePlan.getCurrentLivingSituation());
        carePlanDTO.setSupportsFundedCore(carePlan.getSupportsFundedCore());
        carePlanDTO.setSupportsFundedCapacityBuilding(carePlan.getSupportsFundedCapacityBuilding());
        carePlanDTO.setSupportsFundedCapital(carePlan.getSupportsFundedCapital());
        carePlanDTO.setRiskManagement(carePlan.getRiskManagement());
        carePlanDTO.setExpectedOutcomes(carePlan.getExpectedOutcomes());
        carePlanDTO.setBillingBudgetNotes(carePlan.getBillingBudgetNotes());


        List<CarePlanBilablesDTO> carePlanBilllist = carePlanBillablesService.getBillsByCarePlanID(carePlan);
        carePlanDTO.setCarePlanBillables(carePlanBilllist);

        List<CarePlanSupportScheduleDTO> scheduleList = carePlanSupportScheduleService.getSchedulesByCarePlan(carePlan);
        carePlanDTO.setSupportSchedule(scheduleList);

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
        if (TemporaryHiddenClients.isHidden(clientID)) {
            return List.of();
        }
        Client client = clientRepository.findById(clientID)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientID));

        List<CarePlan> carePlanList = carePlanRepository.findByClient(client).stream()
                .filter(carePlan -> carePlan.getStatus().equalsIgnoreCase("Active"))
                .toList();

        return carePlanList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get CarePlans by careplan id
    public CarePlanDTO getCarePlansByCarePlanID(String carePlanID) {
        CarePlanDTO dto = carePlanRepository.findById(carePlanID)
                .map(this::toDTO) // Map the entity to DTO
                .orElseThrow(() -> new RuntimeException());
        if (TemporaryHiddenClients.isHidden(dto.getClientID())) {
            throw new RuntimeException("Care plan not found");
        }
        return dto;
    }

    // Get all CarePlans
    public List<CarePlanDTO> getAllCarePlans() {
        List<CarePlan> carePlanList = carePlanRepository.findAll().stream().filter(carePlan -> carePlan.getStatus().equalsIgnoreCase("Active")).toList();
        return carePlanList.stream()
                .filter(carePlan -> carePlan.getClientID() == null
                        || !TemporaryHiddenClients.isHidden(carePlan.getClientID().getClientID()))
                .map(this::toDTO)
                .toList();
    }

    // Create a new CarePlan
    public CarePlanDTO createCarePlan(CarePlanDTO carePlanDTO) {
        CarePlan carePlan = toEntity(carePlanDTO);
        String lastCarePlanID = carePlanRepository.getLastIdOfCarePlan();
        String newCarePlanID = Micro_utils.idGenerator("careplan",lastCarePlanID);
        carePlan.setCareplanID(newCarePlanID);

        if (TemporaryHiddenClients.isHidden(carePlanDTO.getClientID())) {
            throw new RuntimeException("Client not found with ID: " + carePlanDTO.getClientID());
        }
        Client clientValue = clientRepository.findById(carePlanDTO.getClientID())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + carePlanDTO.getClientID()));
        carePlan.setClientID(clientValue);

        CarePlanStatus carePlanStatusValue = carePlanStatusRepository.findById(carePlanDTO.getCarePlanStatusID())
                .orElseThrow(() -> new RuntimeException("Care Plan Status not found with ID: " + carePlanDTO.getCarePlanStatusID()));
        carePlan.setCarePlanStatus(carePlanStatusValue);
        carePlan.setStatus("Active");

        carePlanRepository.save(carePlan);

        if (carePlanDTO.getSupportSchedule() != null) {
            for (CarePlanSupportScheduleDTO schedule : carePlanDTO.getSupportSchedule()) {
                schedule.setCareplanID(newCarePlanID);
                carePlanSupportScheduleService.createSchedule(schedule);
            }
        }

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
        // Find existing CarePlan
        CarePlan carePlan = carePlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CarePlan not found"));

        // Update basic details
        carePlan.setStartDate(carePlanDTO.getStartDate());
        carePlan.setTitle(carePlanDTO.getTitle());
        carePlan.setEndDate(carePlanDTO.getEndDate());
        carePlan.setCurrentLivingSituation(carePlanDTO.getCurrentLivingSituation());
        carePlan.setSupportsFundedCore(carePlanDTO.getSupportsFundedCore());
        carePlan.setSupportsFundedCapacityBuilding(carePlanDTO.getSupportsFundedCapacityBuilding());
        carePlan.setSupportsFundedCapital(carePlanDTO.getSupportsFundedCapital());
        carePlan.setRiskManagement(carePlanDTO.getRiskManagement());
        carePlan.setExpectedOutcomes(carePlanDTO.getExpectedOutcomes());
        carePlan.setBillingBudgetNotes(carePlanDTO.getBillingBudgetNotes());

        // Update associated Client
        if (TemporaryHiddenClients.isHidden(carePlanDTO.getClientID())) {
            throw new RuntimeException("Client not found with ID: " + carePlanDTO.getClientID());
        }
        Client clientValue = clientRepository.findById(carePlanDTO.getClientID())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + carePlanDTO.getClientID()));
        carePlan.setClientID(clientValue);

        // Update CarePlan Status
        CarePlanStatus carePlanStatusValue = carePlanStatusRepository.findById(carePlanDTO.getCarePlanStatusID())
                .orElseThrow(() -> new RuntimeException("Care Plan Status not found with ID: " + carePlanDTO.getCarePlanStatusID()));
        carePlan.setCarePlanStatus(carePlanStatusValue);

        // Save updated CarePlan
        carePlanRepository.save(carePlan);

        // --- Support Schedule Management ---
        List<CarePlanSupportSchedule> existingSchedules = carePlanSupportScheduleRepository.findAllByCarePlan(carePlan);
        List<String> incomingScheduleIDs = (carePlanDTO.getSupportSchedule() == null)
                ? new ArrayList<>()
                : carePlanDTO.getSupportSchedule().stream()
                .map(CarePlanSupportScheduleDTO::getScheduleID)
                .filter(scheduleID -> scheduleID != null && !scheduleID.isEmpty())
                .collect(Collectors.toList());

        for (CarePlanSupportSchedule existingSchedule : existingSchedules) {
            if (!incomingScheduleIDs.contains(existingSchedule.getScheduleID())) {
                carePlanSupportScheduleRepository.delete(existingSchedule);
            }
        }

        if (carePlanDTO.getSupportSchedule() != null) {
            for (CarePlanSupportScheduleDTO scheduleDTO : carePlanDTO.getSupportSchedule()) {
                scheduleDTO.setCareplanID(id);
                if (scheduleDTO.getScheduleID() == null || scheduleDTO.getScheduleID().isEmpty()) {
                    carePlanSupportScheduleService.createSchedule(scheduleDTO);
                } else {
                    carePlanSupportScheduleService.updateSchedule(scheduleDTO.getScheduleID(), scheduleDTO);
                }
            }
        }

        // --- Long-Term Goals Management ---
        List<CarePlanLongTermGoal> existingLongTermGoals = carePlanLongTermRepository.findLongTermCarePlansByCarePlanID(carePlan);
        List<CarePlanLongTermGoalDTO> incomingLongTermGoals =
                carePlanDTO.getCarePlanLongTermGoals() != null ? carePlanDTO.getCarePlanLongTermGoals() : new ArrayList<>();
        List<String> incomingLongTermGoalIDs = incomingLongTermGoals.stream()
                .map(CarePlanLongTermGoalDTO::getLongGoalID)
                .filter(longGoalID -> longGoalID != null && !longGoalID.isEmpty())
                .collect(Collectors.toList());

        // Delete removed long-term goals and their associated short-term goals
        for (CarePlanLongTermGoal existingGoal : existingLongTermGoals) {
            if (!incomingLongTermGoalIDs.contains(existingGoal.getLongGoalID())) {
                List<CarePlanShortTermGoal> associatedShortTermGoals = carePlanShortTermRepository.findbyLongTermGoalID(existingGoal);
                for (CarePlanShortTermGoal shortTermGoal : associatedShortTermGoals) {
                    carePlanShortTermRepository.delete(shortTermGoal);
                }
                carePlanLongTermRepository.delete(existingGoal);
            }
        }

        // Add or update long-term and short-term goals
        for (CarePlanLongTermGoalDTO lPlan : incomingLongTermGoals) {
            if (lPlan.getLongGoalID() == null || lPlan.getLongGoalID().isEmpty()) {
                String lastLongTermCarePlanID = carePlanLongTermRepository.getLastIdOfLongTermCarePlan();
                String newLongTermCarePlanID = Micro_utils.idGenerator("longTermCarePlan", lastLongTermCarePlanID);
                lPlan.setCareplanID(id);
                lPlan.setLongGoalID(newLongTermCarePlanID);
            }
            carePlanLongTermGoalService.saveOrUpdate(lPlan);

            CarePlanLongTermGoal longTermGoalEntity = carePlanLongTermRepository.findById(lPlan.getLongGoalID())
                    .orElseThrow(() -> new RuntimeException("Long term goal not found with ID: " + lPlan.getLongGoalID()));

            List<CarePlanShortTermGoalDTO> incomingShortGoals =
                    lPlan.getCarePlanShortTermGoals() != null ? lPlan.getCarePlanShortTermGoals() : new ArrayList<>();
            List<String> incomingShortGoalIDs = incomingShortGoals.stream()
                    .map(CarePlanShortTermGoalDTO::getShortTermGoalID)
                    .filter(shortGoalID -> shortGoalID != null && !shortGoalID.isEmpty())
                    .collect(Collectors.toList());

            List<CarePlanShortTermGoal> existingShortGoals = carePlanShortTermRepository.findbyLongTermGoalID(longTermGoalEntity);
            for (CarePlanShortTermGoal existingShortGoal : existingShortGoals) {
                if (!incomingShortGoalIDs.contains(existingShortGoal.getShortTermGoalID())) {
                    carePlanShortTermRepository.delete(existingShortGoal);
                }
            }

            for (CarePlanShortTermGoalDTO sPlan : incomingShortGoals) {
                if (sPlan.getShortTermGoalID() == null || sPlan.getShortTermGoalID().isEmpty()) {
                    String lastShortTermCarePlanID = carePlanShortTermRepository.getLastIdOfShortTermCarePlan();
                    String newShortTermCarePlanID = Micro_utils.idGenerator("shortTermCarePlan", lastShortTermCarePlanID);
                    sPlan.setCareplanID(id);
                    sPlan.setLongTermGoalID(lPlan.getLongGoalID());
                    sPlan.setShortTermGoalID(newShortTermCarePlanID);
                } else {
                    sPlan.setCareplanID(id);
                    sPlan.setLongTermGoalID(lPlan.getLongGoalID());
                }
                carePlanShortTermGoalService.saveOrUpdate(sPlan);
            }
        }

        // --- Billables Management ---
        List<CarePlanBilables> existingBillables = carePlanBillableRepository.findAllBillablesByCarePlanID(carePlan);
        List<String> incomingBillableIDs = (carePlanDTO.getCarePlanBillables() == null)
                ? new ArrayList<>()
                : carePlanDTO.getCarePlanBillables().stream()
                .map(CarePlanBilablesDTO::getBillID)
                .filter(billID -> billID != null && !billID.isEmpty())
                .collect(Collectors.toList());

        // Delete removed billables
        for (CarePlanBilables existingBill : existingBillables) {
            if (!incomingBillableIDs.contains(existingBill.getBillID())) {
                carePlanBillableRepository.delete(existingBill);
            }
        }

        // Add or update billables
        if (carePlanDTO.getCarePlanBillables() != null) {
            for (CarePlanBilablesDTO bill : carePlanDTO.getCarePlanBillables()) {
                if (bill.getBillID() == null || bill.getBillID().isEmpty()) {
                    bill.setCareplanID(id);
                    carePlanBillablesService.createCarePlanBill(bill);
                } else {
                    carePlanBillablesService.updateCarePlanBill(bill.getBillID(), bill);
                }
            }
        }

        // Return refreshed CarePlan as DTO
        return getCarePlansByCarePlanID(id);
    }

    public void deactivateCarePlan(String carePlanID){
        Optional<CarePlan> carePlan= carePlanRepository.findById(carePlanID);
        if (carePlan.isPresent()){
            CarePlan existingCarePlan = carePlan.get();
            existingCarePlan.setStatus("Inactive");
            carePlanRepository.save(existingCarePlan);
        }
    }


    // Delete a CarePlan
    public void deleteCarePlan(String id) {
        carePlanRepository.deleteById(id);
    }
}
