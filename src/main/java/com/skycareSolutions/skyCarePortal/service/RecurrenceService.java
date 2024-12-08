package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.RecurrentAppointmentDTO;
import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.model.RecurrentAppointment;
import com.skycareSolutions.skyCarePortal.model.RecurrentWork;
import com.skycareSolutions.skyCarePortal.repository.RecurrentAppointmentRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecurrenceService {

    @Autowired
    private RecurrentAppointmentRepository recurrentAppointmentRepository;

    @Autowired
    private RecurrentWorkService recurrentWorkService;

    @Autowired
    private AppointmentService appointmentService;



    public List<RecurrentAppointment> generateRecurrentAppointments(RecurrentWork recurrentWork, Appointment originalAppointment) {
        List<RecurrentAppointment> recurrentAppointments = new ArrayList<>();

        // Fetch recurrence details from RecurrentWork
        String recurrenceType = recurrentWork.getRecurrenceType(); // "DAILY", "WEEKLY", "MONTHLY"
        LocalDate startDate = recurrentWork.getStartDate(); // Start date of the first recurrence
        LocalDate endDate = recurrentWork.getEndDate(); // End date for recurrence (optional)
        int frequencyCount = recurrentWork.getFrequencyCount(); // Every N days/weeks/months
        int occurrenceLimit = recurrentWork.getOccurrenceLimit(); // Maximum number of occurrences

        // Define the recurrence pattern and occurrence number
        int occurrenceNumber = 1;

        // Start creating recurrent appointments
        LocalDate currentStartDate = startDate;
        LocalDate currentEndDate = endDate;

        int occuranceTime = 1;

        String lastRecurrentAppointmentID = recurrentAppointmentRepository.getLastIdOfRecurrentAppointment();
        lastRecurrentAppointmentID = Micro_utils.idGenerator("recurrentAppointments",lastRecurrentAppointmentID);

        System.out.println("recurrent is here ");

        // Loop through and create recurrent appointments
        while ((occurrenceLimit == 0 || occurrenceNumber <= occurrenceLimit) &&
                (endDate == null || currentStartDate.isBefore(endDate) || currentStartDate.isEqual(endDate))) {
            // Calculate the appointment start and end time for the current recurrence

            System.out.println("recurrent ongoing start"+startDate+"   end "+endDate+" occ Numer"+occurrenceNumber);

            RecurrentAppointment recurrentAppointment = new RecurrentAppointment();

            recurrentAppointment.setRecurrentAppointmentID(lastRecurrentAppointmentID); // Use a method to generate unique IDs
            recurrentAppointment.setAppointment(originalAppointment); // Link to original appointment
            recurrentAppointment.setRecurrentWork(recurrentWork); // Link to recurrent work
            recurrentAppointment.setOccurrenceNumber(occurrenceNumber);
            recurrentAppointment.setStartDate(currentStartDate); // Set start date for current occurrence
            recurrentAppointment.setStartTime(originalAppointment.getStartTime()); // Use original start time
            recurrentAppointment.setEndDate(currentEndDate); // End date (could vary, assuming same for simplicity)
            recurrentAppointment.setEndTime(originalAppointment.getEndTime()); // Use original end time
            recurrentAppointment.setStatus("Scheduled"); // Initial status can be "Scheduled"
            recurrentAppointment.setCancelled(false);
            recurrentAppointment.setComment("");

            // Add the generated appointment to the list
            recurrentAppointments.add(recurrentAppointment);

            // Move to the next recurrence based on the recurrence type
            if (recurrenceType.equals("Daily")) {
                currentStartDate = currentStartDate.plusDays(frequencyCount); // Increment by days
                currentEndDate = currentEndDate.plusDays(frequencyCount);
            } else if (recurrenceType.equals("Weekly")) {
                currentStartDate = currentStartDate.plusWeeks(frequencyCount); // Increment by weeks
                currentEndDate = currentEndDate.plusWeeks(frequencyCount);
            } else if (recurrenceType.equals("Monthly")) {
                currentStartDate = currentStartDate.plusMonths(frequencyCount); // Increment by months
                currentEndDate = currentEndDate.plusMonths(frequencyCount);
            }

            occurrenceNumber++; // Increment the occurrence number
            lastRecurrentAppointmentID= Micro_utils.idGenerator("recurrentAppointments",lastRecurrentAppointmentID);
        }

        return recurrentAppointments;
    }

    public void createRecurrentAppointment(String appointmentID, String recurrentID){
        try {
            Appointment appointment = appointmentService.getAppointmentByIdWithEntity(appointmentID);
            RecurrentWork recurrentWork = recurrentWorkService.getRecurrentWorkByIDWithEntity(recurrentID);

            List<RecurrentAppointment> recurrentTask = generateRecurrentAppointments(recurrentWork,appointment);
            System.out.println("arry length "+recurrentTask.size());

            for (RecurrentAppointment app:recurrentTask){
                System.out.println("app id "+app.getRecurrentAppointmentID());
                recurrentAppointmentRepository.saveAll(recurrentTask);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all recurrent appointments by Appointment ID.
     */
    public List<RecurrentAppointmentDTO> getRecurrentAppointmentsByAppointmentID(String appointmentID) {
        return recurrentAppointmentRepository.findAllByAppointmentID(appointmentID)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Get a recurrent appointment by ID.
     */
    public RecurrentAppointmentDTO getRecurrentAppointmentByID(String recurrentAppointmentID) {
        RecurrentAppointment recurrentAppointment = recurrentAppointmentRepository.findById(recurrentAppointmentID)
                .orElseThrow(() -> new RuntimeException("Recurrent Appointment not found"));
        return mapToDTO(recurrentAppointment);
    }

    /**
     * Delete all recurrent appointments for a given appointment ID.
     *
     * @param appointmentID ID of the original appointment.
     */
    public void deleteRecurrentAppointmentsByAppointmentID(String appointmentID) {
        recurrentAppointmentRepository.deleteByAppointmentID(appointmentID);
    }

    /**
     * Delete a specific recurrent appointment by its ID.
     *
     * @param recurrentAppointmentID ID of the recurrent appointment.
     */
    public void deleteRecurrentAppointmentByID(String recurrentAppointmentID) {
        recurrentAppointmentRepository.deleteById(recurrentAppointmentID);
    }

    /**
     * Update a recurrent appointment.
     *
     * @param updatedRecurrentAppointment Updated recurrent appointment details.
     * @return Updated RecurrentAppointment.
     */
    public RecurrentAppointment updateRecurrentAppointment(RecurrentAppointmentDTO updatedRecurrentAppointment) {
        Optional<RecurrentAppointment> RecurrentAppointment = recurrentAppointmentRepository.findById(updatedRecurrentAppointment.getRecurrentAppointmentID());

        if (RecurrentAppointment.isPresent()){
            RecurrentAppointment existingRecurrentAppointment = RecurrentAppointment.get();
            existingRecurrentAppointment.setStartDate(updatedRecurrentAppointment.getStartDate());
            existingRecurrentAppointment.setStartTime(updatedRecurrentAppointment.getStartTime());
            existingRecurrentAppointment.setEndDate(updatedRecurrentAppointment.getEndDate());
            existingRecurrentAppointment.setEndTime(updatedRecurrentAppointment.getEndTime());
            existingRecurrentAppointment.setStatus(updatedRecurrentAppointment.getStatus());
            existingRecurrentAppointment.setCancelled(updatedRecurrentAppointment.isCancelled());
            existingRecurrentAppointment.setComment(updatedRecurrentAppointment.getComment());

            return recurrentAppointmentRepository.save(existingRecurrentAppointment);
        }else{
            throw new RuntimeException();
        }
    }

    /**
     * Map entity to DTO.
     */
    private RecurrentAppointmentDTO mapToDTO(RecurrentAppointment recurrentAppointment) {
        RecurrentAppointmentDTO dto = new RecurrentAppointmentDTO();
        dto.setRecurrentAppointmentID(recurrentAppointment.getRecurrentAppointmentID());
        dto.setAppointmentID(recurrentAppointment.getAppointment().getAppointmentID());
        dto.setRecurrentWorkID(recurrentAppointment.getRecurrentWork().getRecurrentWorkID());
        dto.setOccurrenceNumber(recurrentAppointment.getOccurrenceNumber());
        dto.setStartDate(recurrentAppointment.getStartDate());
        dto.setStartTime(recurrentAppointment.getStartTime());
        dto.setEndDate(recurrentAppointment.getEndDate());
        dto.setEndTime(recurrentAppointment.getEndTime());
        dto.setStatus(recurrentAppointment.getStatus());
        dto.setCancelled(recurrentAppointment.isCancelled());
        dto.setComment(recurrentAppointment.getComment());
        return dto;
    }



}
