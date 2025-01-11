package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.AppointmentRepository;
import com.skycareSolutions.skyCarePortal.repository.AppointmentTypesRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AppointmentAttachmentsService appointmentAttachmentsService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentTypeService appointmentTypeService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AppointmentAddressService appointmentAddressService;

    @Autowired
    private CarePlanService carePlanService;

    @Autowired
    private JobAssignerService jobAssignerService;

    @Autowired
    private RecurrentWorkService recurrentWorkService;

    @Autowired
    private CareGiverService careGiverService;

    @Autowired
    @Lazy
    private RecurrenceService recurrenceService;

    public List<AppointmentCalenderDTO> getAllEventsRelatedToDateRange(String startDate,String endDate){
        List<RecurrentAppointmentDTO> appointments = recurrenceService.getAppointmentsWithinGivenDateRange(startDate,endDate);
        List<AppointmentCalenderDTO> eventDTOs = new ArrayList<>();
        for (RecurrentAppointmentDTO ap:appointments){
            Appointment appointment = getAppointmentByIdWithEntity(ap.getAppointmentID());
            appointment.setStartDate(ap.getStartDate());
            appointment.setEndDate(ap.getEndDate());
            appointment.setStartTime(ap.getStartTime());
            appointment.setEndTime(ap.getEndTime());
            AppointmentCalenderDTO calender = toCalenderDTO(appointment);
            calender.setRecurrentAppointmentID(ap.getRecurrentAppointmentID());
            if (!ap.isCancelled()){
                eventDTOs.add(calender);
            }
        }
        return eventDTOs;
    }

    public List<PendingAppointmentDTO> getPendingAppointmentsRelatedToUser(String employeeID){
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalDate endingDate = currentDate.plusYears(1);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        String endDateFormatter = endingDate.format(formatter);

        List<RecurrentAppointmentDTO> appointmentsList = recurrenceService.getAppointmentsWithinGivenDateRange(formattedDate,endDateFormatter);
        List<RecurrentAppointmentDTO> appointments = appointmentsList.stream().filter(ap-> !ap.isCancelled()).toList();
        CareGiverDTO careGiver = careGiverService.getCareGiverByEmployee(employeeID);
        List<PendingAppointmentDTO> result = new ArrayList<>();
        for (RecurrentAppointmentDTO ap:appointments){
            PendingAppointmentDTO singleAppointment = new PendingAppointmentDTO();
            List<JobAssignerDTO> jobAssignerDTO = jobAssignerService.findJobAssignerByRecurrentID(ap.getRecurrentAppointmentID());
            for (JobAssignerDTO job:jobAssignerDTO){
                if (job.getCareGiverID().equalsIgnoreCase(careGiver.getCareGiverID())){
                    if (job.getAcceptanceType().equalsIgnoreCase("Accepted")){
                        LocalDateTime appointmentStartDateTime = LocalDateTime.of(ap.getStartDate(), LocalTime.parse(ap.getStartTime()));
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        // Check if the appointment's start date-time is equal to or after the current date-time
                        if (!appointmentStartDateTime.isBefore(currentDateTime)) {
                            Appointment appointment = getAppointmentByIdWithEntity(ap.getAppointmentID());
                            singleAppointment.setAppointmentData(mapToDTO(appointment));
                            singleAppointment.setRecurrentAppointmentID(ap.getRecurrentAppointmentID());
                            singleAppointment.setCareGiverStatus(job.getAcceptanceType());
                            singleAppointment.setStartDate(ap.getStartDate().toString());
                            singleAppointment.setEndDate(ap.getEndDate().toString());
                            singleAppointment.setStartTime(ap.getStartTime());
                            singleAppointment.setEndTime(ap.getEndTime());
                            result.add(singleAppointment);
                        }
                    }
                }
            }
        }
        return result;
    }

    public List<AppointmentCalenderDTO> getAllEventsRelatedToDateRangeWithUser(String employeeID,String startDate,String endDate){
        List<RecurrentAppointmentDTO> appointments = recurrenceService.getAppointmentsWithinGivenDateRange(startDate,endDate);
        List<AppointmentCalenderDTO> eventDTOs = new ArrayList<>();
        CareGiverDTO careGiver = careGiverService.getCareGiverByEmployee(employeeID);
        for (RecurrentAppointmentDTO ap:appointments){
             List<JobAssignerDTO> jobAssignerDTO = jobAssignerService.findJobAssignerByRecurrentID(ap.getRecurrentAppointmentID());
             for (JobAssignerDTO job:jobAssignerDTO){
                 if (job.getCareGiverID().equalsIgnoreCase(careGiver.getCareGiverID())){
                     Appointment appointment = getAppointmentByIdWithEntity(ap.getAppointmentID());
                     appointment.setStartDate(ap.getStartDate());
                     appointment.setEndDate(ap.getEndDate());
                     appointment.setStartTime(ap.getStartTime());
                     appointment.setEndTime(ap.getEndTime());
                     AppointmentCalenderDTO calender = toCalenderDTO(appointment);
                     calender.setRecurrentAppointmentID(ap.getRecurrentAppointmentID());
                     if (!ap.isCancelled()){
                         eventDTOs.add(calender);
                     }
                 }
             }
        }
        return eventDTOs;
    }

    public PendingAppointmentDTO getSelectedRecurrentAppointment(String id){
        RecurrentAppointmentDTO recurrentAppointmentDTO = recurrenceService.getRecurrentAppointmentByID(id);
        PendingAppointmentDTO result = new PendingAppointmentDTO();
        result.setRecurrentAppointmentID(id);
        result.setStartDate(recurrentAppointmentDTO.getStartDate().toString());
        result.setEndDate(recurrentAppointmentDTO.getEndDate().toString());
        result.setStartTime(recurrentAppointmentDTO.getStartTime());
        result.setEndTime(recurrentAppointmentDTO.getEndTime());

        Optional<Appointment> appointment = appointmentRepository.findById(recurrentAppointmentDTO.getAppointmentID());
        if (appointment.isPresent()){
            result.setAppointmentData(mapToDTO(appointment.get()));
        }
        return result;
    }

    public int getAppointmentCountBasedOnAppointmentType(String appointmentTypeID){
        AppointmentType appointmentType = appointmentTypeService.getAppointmentTypeByIdWithEntity(appointmentTypeID);
        return appointmentRepository.findAppointmentCountBasedOnAppointmentType(appointmentType);
    }

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAllAppointments()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO getAppointmentById(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        return mapToDTO(appointment);
    }

    public List<AppointmentDTO> getAppointmentsbyClientID(String clientID){
        Client clientInfo = clientService.getClientByIdWithEntitiy(clientID);
        if (clientInfo!=null){
            List<Appointment> appointments = appointmentRepository.findAppointmentsByClient(clientInfo);
            List<AppointmentDTO> appointmentDTOS = new ArrayList<>();
            for (Appointment app:appointments){
                appointmentDTOS.add(mapToDTO(app));
            }
            return appointmentDTOS;
        }else{
            throw new RuntimeException();
        }

    }


    public Appointment getAppointmentByIdWithEntity(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        return appointment;
    }

    public NextAppointmentCareGiverDTO getNextAssignedAppointmentToCareGiver(String employeeID){
        CareGiverDTO careGiver = careGiverService.getCareGiverByEmployee(employeeID);
        List<JobAssignerDTO> jobs = jobAssignerService.assignedJobsBasedOnCareGiverAndStatus(careGiver.getCareGiverID(),"Accepted");
        List<RecurrentAppointmentDTO> appointmentDTOS = new ArrayList<>();
        for (JobAssignerDTO job:jobs){
            RecurrentAppointmentDTO appointment = recurrenceService.getRecurrentAppointmentByID(job.getRecurrentAppointmentID());
            appointmentDTOS.add(appointment);
        }
        appointmentDTOS.removeIf(appointment->!appointment.getStatus().equalsIgnoreCase("Scheduled"));

        appointmentDTOS.sort(Comparator.comparing(RecurrentAppointmentDTO::getStartDate)
                .thenComparing(RecurrentAppointmentDTO::getStartTime));

        if (!appointmentDTOS.isEmpty()){
            NextAppointmentCareGiverDTO nextAppointmentCareGiverDTO = new NextAppointmentCareGiverDTO();
            nextAppointmentCareGiverDTO.setAppointment(getAppointmentById(appointmentDTOS.get(0).getAppointmentID()));
            nextAppointmentCareGiverDTO.setRecurrentTask(recurrenceService.getRecurrentAppointmentByID(appointmentDTOS.get(0).getRecurrentAppointmentID()));
            return nextAppointmentCareGiverDTO;
        }else{
            return new NextAppointmentCareGiverDTO();
        }
    }

    public List<CareGiverAllocatedAppointmentsDTO> getAppointmentsAssignedToCareGiver(String employeeID, String state) {
        CareGiverDTO careGiver = careGiverService.getCareGiverByEmployee(employeeID);
        List<JobAssignerDTO> jobs = jobAssignerService.assignedJobsBasedOnCareGiverAndStatus(careGiver.getCareGiverID(), state);
        List<CareGiverAppointmentDTO> result = new ArrayList<>();
        Set<String> addedAppointmentIDs = new HashSet<>();
        HashMap<String, JobAssignerDTO[]> appointmentAllocationMap = new HashMap<>();
        List<CareGiverAllocatedAppointmentsDTO> allocationList =  new ArrayList<>();

        for (JobAssignerDTO job : jobs) {
            String appointmentID = job.getAppointmentID();
            if (appointmentAllocationMap.containsKey(appointmentID)) {
                // Get the existing array
                JobAssignerDTO[] existingArray = appointmentAllocationMap.get(appointmentID);

                // Create a new array with an additional slot
                JobAssignerDTO[] updatedArray = Arrays.copyOf(existingArray, existingArray.length + 1);

                // Add the new job to the array
                updatedArray[existingArray.length] = job;

                // Put the updated array back into the map
                appointmentAllocationMap.put(appointmentID, updatedArray);
            } else {
                // Create a new array with the job
                appointmentAllocationMap.put(appointmentID, new JobAssignerDTO[]{job});
            }
        }
        for (Map.Entry<String, JobAssignerDTO[]> entry : appointmentAllocationMap.entrySet()) {
            CareGiverAllocatedAppointmentsDTO singleAllocation = new CareGiverAllocatedAppointmentsDTO();
            AppointmentDTO appointmentDTO = getAppointmentById(entry.getKey());
            singleAllocation.setCareGiverID(careGiver.getCareGiverID());
            singleAllocation.setAppointmentData(appointmentDTO);

            JobAssignerDTO[] jobList = entry.getValue();
            List<CareGiverAllocationResponseDTO> subRecurrentAllocationList = new ArrayList<>();
            for (JobAssignerDTO job:jobList){
                CareGiverAllocationResponseDTO careGiverAllocationResponseDTO = new CareGiverAllocationResponseDTO();
                int totalRequiredCareGiverCount = appointmentDTO.getCaregiverCount();
                int acceptedCount = jobAssignerService.careGiverCountByAppointmentAcceptanceType(appointmentDTO.getAppointmentID(), "Accepted", job.getRecurrentAppointmentID());
                if (totalRequiredCareGiverCount>acceptedCount){
                    RecurrentAppointmentDTO recurrentAppointmentDTO = recurrenceService.getRecurrentAppointmentByID(job.getRecurrentAppointmentID());
                    careGiverAllocationResponseDTO.setRecurrentAppointmentData(recurrentAppointmentDTO);
                    careGiverAllocationResponseDTO.setJobAssignData(job);
                    careGiverAllocationResponseDTO.setTotalRequired(totalRequiredCareGiverCount);
                    careGiverAllocationResponseDTO.setPendingRequired(totalRequiredCareGiverCount-acceptedCount);
                    subRecurrentAllocationList.add(careGiverAllocationResponseDTO);
                }

            }
            singleAllocation.setAllocations(subRecurrentAllocationList);
            allocationList.add(singleAllocation);
        }
        Iterator<CareGiverAllocatedAppointmentsDTO> iterator = allocationList.iterator();
        while (iterator.hasNext()) {
            CareGiverAllocatedAppointmentsDTO item = iterator.next();
            if (item.getAllocations().isEmpty()) {
                iterator.remove(); // Safely remove the item
            }
        }
        return allocationList;
    }




    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO, List<MultipartFile> files) {
        String lastAppointmentID = appointmentRepository.getLastIdOfAppointments();
        String newAppointmentID = Micro_utils.idGenerator("appointment",lastAppointmentID);
        appointmentDTO.setAppointmentID(newAppointmentID);

        if (appointmentDTO.getAppointmentAddress().getAddress()!=null && appointmentDTO.getAppointmentAddress().getAddress()!=""){
            appointmentAddressService.save(appointmentDTO.getAppointmentAddress());
        }
        Appointment appointment = mapToEntity(appointmentDTO);
        appointment = appointmentRepository.save(appointment);

        if (appointmentDTO.getRecurrenceState()){
            appointmentDTO.getRecurrentWork().setAppointmentID(newAppointmentID);
            RecurrentWorkDTO recurrentWorkDTO =  recurrentWorkService.createRecurrentWork(appointmentDTO.getRecurrentWork());
            recurrenceService.createRecurrentAppointment(appointment.getAppointmentID(),recurrentWorkDTO.getRecurrentWorkID());
        }else{
            RecurrentWorkDTO recurrentWorkDTO  = new RecurrentWorkDTO();
            recurrentWorkDTO.setAppointmentID(appointment.getAppointmentID());
            recurrentWorkDTO.setStartDate(appointment.getStartDate());
            recurrentWorkDTO.setEndDate(appointment.getEndDate());
            recurrentWorkDTO.setRecurrenceType("Once");
            recurrentWorkDTO.setOccurrenceLimit(1);

            System.out.println("year "+appointment.getStartDate().getYear()+" Month "+appointment.getStartDate().getMonth());

            LocalDate date  = LocalDate.of(appointment.getStartDate().getYear(),appointment.getStartDate().getMonthValue(),appointment.getStartDate().getDayOfMonth());
            DayOfWeek dayOfWeek  = date.getDayOfWeek();
            String formattedDay = dayOfWeek.toString().toLowerCase(); // Convert to lowercase
            formattedDay = formattedDay.substring(0, 1).toUpperCase() + formattedDay.substring(1); // Capitalize first letter
            recurrentWorkDTO.setDay(formattedDay);
            RecurrentWorkDTO recurrentWork =  recurrentWorkService.createRecurrentWork(recurrentWorkDTO);
            recurrenceService.createRecurrentAppointment(appointment.getAppointmentID(),recurrentWork.getRecurrentWorkID());
        }

        appointmentDTO.getJobAssigns().setAppointmentID(newAppointmentID);
        jobAssignerService.createJobAssigner(appointmentDTO.getJobAssigns(),appointmentDTO.getBroadcastType());

        AppointmentAttachmentsDTO attachmentInfo = new AppointmentAttachmentsDTO();
        attachmentInfo.setAppointmentID(newAppointmentID);

        if (files!=null && files.size()>0){
            appointmentAttachmentsService.createAttachements(attachmentInfo,files);
        }


        return mapToDTO(appointment);
    }

    public AppointmentDTO updateAppointment(String id, AppointmentDTO appointmentDTO, List<MultipartFile> files) {
        // Retrieve the existing appointment entity
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));

        // Update basic fields
        appointment.setTitle(appointmentDTO.getTitle());
        appointment.setBroadcastType(appointmentDTO.getBroadcastType());
        appointment.setComment(appointmentDTO.getComment());
        appointment.setDuration(appointmentDTO.getDuration());
        appointment.setStartDate(LocalDate.parse(appointmentDTO.getStartDate()));
        appointment.setStartTime(appointmentDTO.getStartTime());
        appointment.setEndDate(LocalDate.parse(appointmentDTO.getEndDate()));
        appointment.setEndTime(appointmentDTO.getEndTime());
        appointment.setCaregiverCount(appointmentDTO.getCaregiverCount());

        // Update AppointmentType
        appointment.setAppointmentType(
                appointmentTypeService.getAppointmentTypeByIdWithEntity(appointmentDTO.getAppointmentTypeID())
        );

        // Update Client
        Client client = clientService.getClientByIdWithEntitiy(appointmentDTO.getClientID());
        appointment.setClient(client);

        // Update AppointmentAddress
        if (appointmentDTO.getAppointmentAddress() != null) {
            appointmentAddressService.findById(appointmentDTO.getAppointmentAddress().getAppointmentAddressID())
                    .ifPresentOrElse(
                            appointment::setAppointmentAddress,
                            () -> {
                                throw new RuntimeException("AppointmentAddress not found for ID: "
                                        + appointmentDTO.getAppointmentAddress().getAppointmentAddressID());
                            }
                    );
        }

        // Update CarePlan if present
        if (appointmentDTO.getCarePlanID() != null && !appointmentDTO.getCarePlanID().isEmpty()) {
            carePlanService.findById(appointmentDTO.getCarePlanID())
                    .ifPresentOrElse(
                            appointment::setCarePlan,
                            () -> {
                                throw new RuntimeException("CarePlan not found for ID: "
                                        + appointmentDTO.getCarePlanID());
                            }
                    );
        }

        // Save the updated appointment
        appointment = appointmentRepository.save(appointment);

        RecurrentWorkDTO recurrentWorkDTO = recurrentWorkService.getRecurrentWorkTaskByAppointment(id);
        if (recurrentWorkDTO!=null){

            if (appointmentDTO.getRecurrenceState()){
                recurrentWorkDTO.setTaskID(appointmentDTO.getTaskID());
                recurrentWorkDTO.setEndDate(appointmentDTO.getRecurrentWork().getEndDate());
                recurrentWorkDTO.setStartDate(appointmentDTO.getRecurrentWork().getStartDate());
                recurrentWorkDTO.setDay(appointmentDTO.getRecurrentWork().getDay());
                recurrentWorkDTO.setFrequencyCount(appointmentDTO.getRecurrentWork().getFrequencyCount());
                recurrentWorkDTO.setOccurrenceLimit(appointmentDTO.getRecurrentWork().getOccurrenceLimit());
                recurrentWorkDTO.setRecurrenceType(appointmentDTO.getRecurrentWork().getRecurrenceType());

                recurrentWorkService.updateRecurrentWork(recurrentWorkDTO.getRecurrentWorkID(),recurrentWorkDTO);
            }else{
//                recurrentWorkService.deleteRecurrentWork(recurrentWorkDTO.getRecurrentWorkID());
            }
        }else{
            if (appointmentDTO.getRecurrenceState()){
                appointmentDTO.getRecurrentWork().setAppointmentID(id);
                recurrentWorkService.createRecurrentWork(appointmentDTO.getRecurrentWork());
            }
        }

        List<JobAssignerDTO> alreadyAssigners = jobAssignerService.getAllJobsRelatedToAppointment(id);
        List<String> newCareGivers = new ArrayList<>();
        if (appointmentDTO.getBroadcastType().equalsIgnoreCase("All Caregivers")){
            List<CareGiverDTO> careGivers = careGiverService.getAllCareGivers();
            for (CareGiverDTO careGiver:careGivers){
                JobAssignerDTO matchedAssigner = alreadyAssigners.stream()
                        .filter(assigner -> assigner.getCareGiverID().equals(careGiver.getCareGiverID()))
                        .findFirst()
                        .orElse(null);
                if (matchedAssigner==null){
                    newCareGivers.add(careGiver.getCareGiverID());
                }
            }
            String[] careGiverIDs = new String[newCareGivers.size()];
            int index=0;
            for (String careGiverID:newCareGivers){
                System.out.println("careGivers"+careGiverID);
                careGiverIDs[index]=careGiverID;
            }

            appointmentDTO.getJobAssigns().setCareGiverIDs(careGiverIDs);
            jobAssignerService.createJobAssigner(appointmentDTO.getJobAssigns(),"Selected Caregivers");
        }else{
            for(String jober:appointmentDTO.getJobAssigns().getCareGiverIDs()){
                JobAssignerDTO matchedAssigner = alreadyAssigners.stream()
                        .filter(assigner -> assigner.getCareGiverID().equals(jober))
                        .findFirst()
                        .orElse(null);
                if (matchedAssigner==null){
                    newCareGivers.add(jober);
                }
            }
            List<JobAssignerDTO> toRemove = new ArrayList<>();

            for (JobAssignerDTO assigned : alreadyAssigners) {
                boolean isPresent = Arrays.stream(appointmentDTO.getJobAssigns().getCareGiverIDs())
                        .anyMatch(person -> person.equals(assigned.getCareGiverID()));

                if (!isPresent) {
                    toRemove.add(assigned);
                }
            }

            for (JobAssignerDTO remover : toRemove) {
                String jobID = alreadyAssigners.stream().filter(alreadyAssigner->alreadyAssigner.getCareGiverID().equalsIgnoreCase(remover.getCareGiverID())).
                        map(JobAssignerDTO::getAssignID).findFirst().orElse(null);
                jobAssignerService.deleteJobAssigner(jobID);
            }
        }

        // Update attachments
        AppointmentAttachmentsDTO attachmentInfo = new AppointmentAttachmentsDTO();
        attachmentInfo.setAppointmentID(id);

        List<AppointmentAttachmentsDTO> attachmentsDTOS= appointmentAttachmentsService.getAttachmentsbyAppointmentID(id);
        List<AppointmentAttachmentsDTO> toRemove = attachmentsDTOS.stream()
                .filter(attachment -> appointmentDTO.getAttachments().stream()
                        .noneMatch(attachmentData -> attachmentData.getDocumentID().equals(attachment.getDocumentID())))
                .collect(Collectors.toList());

        // Remove the identified attachments
        for (AppointmentAttachmentsDTO attachment : toRemove) {
            appointmentAttachmentsService.deleteAttachment(attachment);
        }

        if (files != null && !files.isEmpty()) {
            appointmentAttachmentsService.createAttachements(attachmentInfo, files);
        }
        return mapToDTO(appointment);
    }


    public void deleteAppointment(String id) {
        appointmentRepository.deleteById(id);
    }

    // Map to DTO
    private AppointmentDTO mapToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAppointmentID(appointment.getAppointmentID());
        dto.setTitle(appointment.getTitle());
        dto.setAppointmentTypeID(appointment.getAppointmentType().getAppointmentTypeID());
        dto.setClientID(appointment.getClient().getClientID());
        dto.setCaregiverCount(appointment.getCaregiverCount());
        dto.setStartDate(appointment.getStartDate().toString());
        dto.setStartTime(appointment.getStartTime());
        dto.setEndDate(appointment.getEndDate().toString());
        dto.setEndTime(appointment.getEndTime());
        dto.setDuration(appointment.getDuration());
        dto.setComment(appointment.getComment());
        dto.setBroadcastType(appointment.getBroadcastType());

        // Map AppointmentAddress
        if (appointment.getAppointmentAddress() != null) {
            AppointmentAddressDTO addressDTO = new AppointmentAddressDTO();
            addressDTO.setAppointmentAddressID(appointment.getAppointmentAddress().getAppointmentAddressID());
            addressDTO.setAddress(appointment.getAppointmentAddress().getAddress());
            addressDTO.setCity(appointment.getAppointmentAddress().getCity());
            addressDTO.setState(appointment.getAppointmentAddress().getState());
            addressDTO.setPostalCode(appointment.getAppointmentAddress().getPostalCode());
            dto.setAppointmentAddress(addressDTO);
        }

        if (appointment.getCarePlan()!=null){
            dto.setCarePlanID(appointment.getCarePlan().getCareplanID());
        }
        List<JobAssignerDTO> jobAssigner = jobAssignerService.getAllJobsRelatedToAppointment(appointment.getAppointmentID());
        JobAssignCreationDTO createDTO = new JobAssignCreationDTO();
        createDTO.setAppointmentID(appointment.getAppointmentID());

        String[] careGiverList = new String[jobAssigner.size()];

        int index = 0;
        for (JobAssignerDTO assigner : jobAssigner) {
            careGiverList[index] = assigner.getCareGiverID();
            System.out.println("care givers "+assigner.getCareGiverID());
            index++;
        }
        createDTO.setCareGiverIDs(careGiverList);
        dto.setJobAssigns(createDTO);

        RecurrentWorkDTO rw = recurrentWorkService.getRecurrentWorkTaskByAppointment(appointment.getAppointmentID());

        if (rw!=null){
            dto.setRecurrenceState(true);
            dto.setRecurrentWork(rw);
        }
        List<AppointmentAttachmentsDTO> attachments = appointmentAttachmentsService.getAttachmentsbyAppointmentID(appointment.getAppointmentID());
        dto.setRecurrenceState(appointment.isRecurrentState());
        dto.setAttachments(attachments);
        return dto;
    }


    // Map to Entity
    private Appointment mapToEntity(AppointmentDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentID(dto.getAppointmentID());
        appointment.setTitle(dto.getTitle());
        appointment.setAppointmentType(appointmentTypeService.getAppointmentTypeByIdWithEntity(dto.getAppointmentTypeID()));

        Client client = clientService.getClientByIdWithEntitiy(dto.getClientID());
        appointment.setClient(client);

        // Set AppointmentAddress
        appointmentAddressService.findById(dto.getAppointmentAddress().getAppointmentAddressID())
                .ifPresentOrElse(
                        appointment::setAppointmentAddress,
                        () -> {
                            throw new RuntimeException("AppointmentAddress not found for ID: " + dto.getAppointmentAddress().getAppointmentAddressID());
                        }
                );

        appointment.setBroadcastType(dto.getBroadcastType());
        appointment.setComment(dto.getComment());
        appointment.setDuration(dto.getDuration());
        appointment.setStartDate(LocalDate.parse(dto.getStartDate()));
        appointment.setStartTime(dto.getStartTime());
        appointment.setEndDate(LocalDate.parse(dto.getEndDate()));
        appointment.setEndTime(dto.getEndTime());
        appointment.setCaregiverCount(dto.getCaregiverCount());
        appointment.setRecurrentState(dto.getRecurrenceState());
        if (dto.getCarePlanID()!=null && !dto.getCarePlanID().equalsIgnoreCase("")){
            System.out.println("care plan id is there "+dto.getCarePlanID());
            carePlanService.findById(dto.getCarePlanID())
                    .ifPresentOrElse(
                            appointment::setCarePlan,
                            () -> {
                                throw new RuntimeException("careplan not found for ID: " + dto.getCarePlanID());
                            }
                    );
        }

//        if (dto.getTaskID()!=null && dto.getTaskID().equalsIgnoreCase("")){
//            carePlanService.findById(dto.getCarePlanID())
//                    .ifPresentOrElse(
//                            appointment::setCarePlan,
//                            () -> {
//                                throw new RuntimeException("careplan not found for ID: " + dto.getCarePlanID());
//                            }
//                    );
//        }



        // Map other fields
        return appointment;
    }

    private AppointmentCalenderDTO toCalenderDTO(Appointment appointment){
        AppointmentCalenderDTO calenderDTO = new AppointmentCalenderDTO();

        calenderDTO.setAppointmentID(appointment.getAppointmentID());
        calenderDTO.setTitle(appointment.getTitle());

        // Assuming startDate and endDate are Date objects and startTime/endTime are Strings
        LocalDateTime startDateTime = appointment.getStartDate().atStartOfDay(); // Defaulting to start of day if no time is provided
        if (appointment.getStartTime() != null) {
            // Adding the time component if available
            String[] timeParts = appointment.getStartTime().split(":");
            startDateTime = startDateTime.withHour(Integer.parseInt(timeParts[0])).withMinute(Integer.parseInt(timeParts[1]));
        }

        LocalDateTime endDateTime = appointment.getEndDate().atStartOfDay(); // Defaulting to start of day if no time is provided
        if (appointment.getEndTime() != null) {
            // Adding the time component if available
            String[] timeParts = appointment.getEndTime().split(":");
            endDateTime = endDateTime.withHour(Integer.parseInt(timeParts[0])).withMinute(Integer.parseInt(timeParts[1]));
        }

        // Format to ISO 8601 format, suitable for React calendar
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String startFormatted = startDateTime.format(formatter);
        String endFormatted = endDateTime.format(formatter);

        // Set the start and end times in the DTO
        calenderDTO.setStartDateAndTime(startFormatted);  // Assuming the method accepts the formatted string
        calenderDTO.setEndDateAndTime(endFormatted);

        calenderDTO.setEventColor(appointment.getAppointmentType().getColor());

        return calenderDTO;
    }
}
