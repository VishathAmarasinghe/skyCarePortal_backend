package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.ShiftNoteRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ShiftNoteService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "shiftNotes").toString();

    @Autowired
    ShiftNoteRepository shiftNoteRepository;

    @Autowired
    @Lazy
    EmployeeService employeeService;

    @Autowired
    @Lazy
    CareGiverService careGiverService;

    @Autowired
    @Lazy
    RecurrenceService recurrenceService;

    @Autowired
    ShiftDocumentService shiftDocumentService;

    @Autowired
    JobAssignerService jobAssignerService;

    @Autowired
    AppointmentService appointmentService;

    @Autowired
    ClientService clientService;

    public void generateReport(String startDate,String endDate,String employeeType, String employeeNo){
        if (employeeType.equalsIgnoreCase("All")){

        }else{
            Employee employee = employeeService.findOneEmployeeWithEntity(employeeNo);
            if (employee!=null){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
                LocalDate endLocalDate = LocalDate.parse(endDate, formatter);
                List<ShiftNotes> shiftNoteList = shiftNoteRepository.getShiftNotesByCreatorAndDateRangeAndState(employee,startLocalDate,endLocalDate);

            }
        }
    }


    public Boolean startShift(StartShiftNoteDTO startShiftNoteDTO){
        String lastShiftNoteID = shiftNoteRepository.getLastIdOfShiftNotes();
        String newShiftNoteID = Micro_utils.idGenerator("shiftNotes",lastShiftNoteID);
        Employee employee = employeeService.findOneEmployeeWithEntity(startShiftNoteDTO.getEmployeeID());
        CareGiver careGiver = careGiverService.getCareGiverByIdWithEntity(careGiverService.getCareGiverByEmployee(employee.getEmployeeID()).getCareGiverID());
        RecurrentAppointment recurrentAppointment = recurrenceService.getRecurrentAppointmentByIDWithEntity(startShiftNoteDTO.getRecurrentAppointmentID());
        ShiftNotes shiftNotes = new ShiftNotes();

        LocalDateTime now = LocalDateTime.now();

        // Format the current date as "YYYY-MM-DD"
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = now.format(dateFormatter);

        // Format the current time as "HH:mm:ss"
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = now.format(timeFormatter);

        shiftNotes.setNoteID(newShiftNoteID);
        shiftNotes.setState("Started");
        shiftNotes.setRecurrentAppointment(recurrentAppointment);
        shiftNotes.setSystemShiftStartDate(LocalDate.parse(formattedDate));
        shiftNotes.setSystemShiftStartTime(formattedTime);
        shiftNotes.setEmployee(employee);
        shiftNotes.setCareGiver(careGiver);
        shiftNoteRepository.save(shiftNotes);
        return true;
    }

    public ShiftNoteStateDTO getShiftNoteState(StartShiftNoteDTO startShiftNoteDTO){
        Employee employee = employeeService.findOneEmployeeWithEntity(startShiftNoteDTO.getEmployeeID());
        CareGiver careGiver = careGiverService.getCareGiverByIdWithEntity(careGiverService.getCareGiverByEmployee(employee.getEmployeeID()).getCareGiverID());
        RecurrentAppointment recurrentAppointment = recurrenceService.getRecurrentAppointmentByIDWithEntity(startShiftNoteDTO.getRecurrentAppointmentID());

        ShiftNotes shiftNotes = shiftNoteRepository.getShiftNoteByRecurrentAppointmentAndCareGiver(recurrentAppointment,careGiver);
        ShiftNoteStateDTO shiftNoteStateDTO = new ShiftNoteStateDTO();
        if (shiftNotes!=null){
            shiftNoteStateDTO.setShiftNoteID(shiftNotes.getNoteID());
            shiftNoteStateDTO.setShiftNoteState(shiftNotes.getState());
            shiftNoteStateDTO.setCareGiverID(shiftNotes.getCareGiver().getCareGiverID());
            shiftNoteStateDTO.setEmployeeID(shiftNotes.getEmployee().getEmployeeID());
            shiftNoteStateDTO.setRecurrentAppointmentID(shiftNotes.getRecurrentAppointment().getRecurrentAppointmentID());
            shiftNoteStateDTO.setShiftNoteAvailability("available");
            return shiftNoteStateDTO;
        }else{
            shiftNoteStateDTO.setShiftNoteAvailability("notAvailable");
            return shiftNoteStateDTO;
        }
    }

    public Boolean newShiftNotes(ShiftNoteDTO shiftNoteDTO,List<MultipartFile> files){
        String lastShiftNoteID = shiftNoteRepository.getLastIdOfShiftNotes();
        String newShiftNoteID = Micro_utils.idGenerator("shiftNotes",lastShiftNoteID);
        Employee employee = employeeService.findOneEmployeeWithEntity(shiftNoteDTO.getEmployeeID());
        shiftNoteDTO.setNoteID(newShiftNoteID);

            ShiftNotes  shiftNote  = new ShiftNotes();
            shiftNote.setNoteID(newShiftNoteID);
            shiftNote.setEmployee(employee);
//            JobAssignerDTO jobAssigner = jobAssignerService.findJobAssignerByCareGiverIDAndRecurrentID(shiftNote.getCareGiver().getCareGiverID(),shiftNote.getRecurrentAppointment().getRecurrentAppointmentID());
            shiftNote.setShiftEndTime(shiftNoteDTO.getShiftEndTime());
            shiftNote.setShiftEndDate(shiftNoteDTO.getShiftEndDate());
            shiftNote.setState("Completed");
            shiftNote.setTitle(shiftNoteDTO.getTitle());
            shiftNote.setShiftStartTime(shiftNoteDTO.getShiftStartTime());
            shiftNote.setShiftStartDate(shiftNoteDTO.getShiftStartDate());
            shiftNote.setNotes(shiftNoteDTO.getNotes());
            shiftNote.setWorkHours(shiftNoteDTO.getTotalWorkHrs());

            if (shiftNoteDTO.getClientID()!=null && !shiftNoteDTO.getClientID().isEmpty()){
                Client client = clientService.getClientByIdWithEntitiy(shiftNoteDTO.getClientID());
                shiftNote.setClient(client);
            }

            if (shiftNoteDTO.getRecurrentAppointmentID()!=null && !shiftNoteDTO.getRecurrentAppointmentID().isEmpty()){
                RecurrentAppointment recurrentAppointment = recurrenceService.getRecurrentAppointmentByIDWithEntity(shiftNoteDTO.getRecurrentAppointmentID());
                shiftNote.setRecurrentAppointment(recurrentAppointment);
                shiftNote.setClient(recurrentAppointment.getAppointment().getClient());
            }
            if (shiftNoteDTO.getCareGiverID()!=null && !shiftNoteDTO.getCareGiverID().isEmpty()){
                CareGiver careGiver = careGiverService.getCareGiverByIdWithEntity(shiftNoteDTO.getCareGiverID());
                shiftNote.setCareGiver(careGiver);
            }

            LocalDateTime now = LocalDateTime.now();

            // Format the current date as "YYYY-MM-DD"
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = now.format(dateFormatter);

            // Format the current time as "HH:mm:ss"
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedTime = now.format(timeFormatter);

            shiftNote.setSystemShiftEndDate(LocalDate.parse(formattedDate));
            shiftNote.setSystemShiftEndTime(formattedTime);

        shiftNote.setSystemShiftStartDate(LocalDate.parse(formattedDate));
        shiftNote.setSystemShiftStartTime(formattedTime);

            shiftNoteRepository.save(shiftNote);

        System.out.println("files "+files);

        try {
            if (files!=null && !files.isEmpty()){
                for (MultipartFile file:files){
                    if (file!=null){
                        String originalFileName = file.getOriginalFilename();
                        String fileExtension = originalFileName;
                        if (originalFileName != null && originalFileName.contains(".")) {
                            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                        }
                        System.out.println("original file name "+originalFileName);
                        File directory = new File(DIRECTORY);
                        if(!directory.exists()){
                            directory.mkdirs();
                        }
                        File destinationFile = new File(DIRECTORY, shiftNoteDTO.getNoteID() + fileExtension);
                        if (destinationFile.exists()) {
                            // Delete the existing file
                            boolean deleted = destinationFile.delete();
                            if (!deleted) {
                                throw new RuntimeException("Failed to delete the existing file: " + destinationFile.getName());
                            }
                        }

                        file.transferTo(destinationFile);
                        ShiftNoteDocumentDTO docs  = new ShiftNoteDocumentDTO();
                        docs.setDocumentName(originalFileName);
                        docs.setShiftNoteID(shiftNoteDTO.getNoteID());
                        docs.setDocumentLocation(destinationFile.getAbsolutePath());
                        shiftDocumentService.saveDocument(docs);
                    }
                }
            }
            return true;

        } catch (Exception e) {
            System.out.println("run time exceptipn");
            throw new RuntimeException(e);
        }
    }

    public Boolean finishShift(ShiftNoteDTO shiftNoteDTO, List<MultipartFile> files){
        Optional<ShiftNotes> exisitingShiftNote = shiftNoteRepository.findById(shiftNoteDTO.getNoteID());

        if (exisitingShiftNote.isPresent()){
            ShiftNotes  shiftNote  = exisitingShiftNote.get();
            JobAssignerDTO jobAssigner = jobAssignerService.findJobAssignerByCareGiverIDAndRecurrentID(shiftNote.getCareGiver().getCareGiverID(),shiftNote.getRecurrentAppointment().getRecurrentAppointmentID());
            shiftNote.setShiftEndTime(shiftNoteDTO.getShiftEndTime());
            shiftNote.setShiftEndDate(shiftNoteDTO.getShiftEndDate());
            shiftNote.setState("Completed");
            shiftNote.setTitle(shiftNoteDTO.getTitle());
            shiftNote.setShiftStartTime(shiftNoteDTO.getShiftStartTime());
            shiftNote.setShiftStartDate(shiftNoteDTO.getShiftStartDate());
            shiftNote.setSystemShiftStartDate(shiftNote.getSystemShiftStartDate());
            shiftNote.setSystemShiftStartTime(shiftNote.getSystemShiftStartTime());
            shiftNote.setSystemShiftStartDate(shiftNoteDTO.getSystemShiftStartDate());
            shiftNote.setNotes(shiftNoteDTO.getNotes());
            shiftNote.setWorkHours(shiftNoteDTO.getTotalWorkHrs());


            LocalDateTime now = LocalDateTime.now();

            // Format the current date as "YYYY-MM-DD"
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = now.format(dateFormatter);

            // Format the current time as "HH:mm:ss"
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedTime = now.format(timeFormatter);

            shiftNote.setSystemShiftEndDate(LocalDate.parse(formattedDate));
            shiftNote.setSystemShiftEndTime(formattedTime);

            shiftNoteRepository.save(shiftNote);
            recurrenceService.updateAppointmentState(shiftNote.getRecurrentAppointment().getRecurrentAppointmentID(),"Completed");
            jobAssignerService.updateJobAcceptanceState("Completed",jobAssigner.getAssignID());
        }

        try {
            if (files!=null && !files.isEmpty()){
                for (MultipartFile file:files){
                    if (file!=null){
                        String originalFileName = file.getOriginalFilename();
                        String fileExtension = originalFileName;
                        if (originalFileName != null && originalFileName.contains(".")) {
                            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                        }
                        System.out.println("original file name "+originalFileName);
                        File directory = new File(DIRECTORY);
                        if(!directory.exists()){
                            directory.mkdirs();
                        }
                        File destinationFile = new File(DIRECTORY, shiftNoteDTO.getNoteID() + fileExtension);
                        if (destinationFile.exists()) {
                            // Delete the existing file
                            boolean deleted = destinationFile.delete();
                            if (!deleted) {
                                throw new RuntimeException("Failed to delete the existing file: " + destinationFile.getName());
                            }
                        }
                        file.transferTo(destinationFile);
                        ShiftNoteDocumentDTO docs  = new ShiftNoteDocumentDTO();
                        docs.setDocumentName(originalFileName);
                        docs.setShiftNoteID(shiftNoteDTO.getNoteID());
                        docs.setDocumentLocation(destinationFile.getAbsolutePath());
                        shiftDocumentService.saveDocument(docs);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ShiftNoteDTO> getAllShiftNotes(){
        return shiftNoteRepository.getAllShiftNotes().stream().map(this::toDTO).toList();
    }

    public List<TimeSheetDTO> getTimeSheetsForGivenDateRange(String startDate,String endDate,String careGiverID,String clientID){
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<ShiftNotes> shiftNoteList = shiftNoteRepository.findShiftNotesBetweenDates(start,end);
        List<TimeSheetDTO> timeSheetList = new ArrayList<>();
        for (ShiftNotes shiftNote:shiftNoteList){
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            TimeSheetDTO timeSheet = new TimeSheetDTO();

            LocalDateTime startDateTime = LocalDateTime.of(
                    shiftNote.getShiftStartDate(),
                    LocalTime.parse(shiftNote.getShiftStartTime(), timeFormatter)
            );
            LocalDateTime endDateTime = LocalDateTime.of(
                    shiftNote.getShiftEndDate(),
                    LocalTime.parse(shiftNote.getShiftEndTime(), timeFormatter)
            );
            long durationInMinutes = Duration.between(startDateTime, endDateTime).toMinutes();
            long hours = durationInMinutes/60;
            long minutes = durationInMinutes%60;

            timeSheet.setTotalHours(String.format("%d hours, %d minutes", hours, minutes));
            timeSheet.setShiftNoteDTO(toDTO(shiftNote));
            if (shiftNote.getCareGiver()!=null){
                EmployeeDTO employee  = employeeService.findOneEmployee(shiftNote.getCareGiver().getEmployee().getEmployeeID());
                timeSheet.setEmployeeDTO(employee);
            }else{
                if (shiftNote.getEmployee()!=null){

                    EmployeeDTO employee  = employeeService.findOneEmployee(shiftNote.getEmployee().getEmployeeID());
                    timeSheet.setEmployeeDTO(employee);
                }
            }

            if (shiftNote.getClient()!=null){
                ClientDTO clientDTO = clientService.getClientById(shiftNote.getClient().getClientID());
                timeSheet.setClient(clientDTO);
            }
            if (shiftNote.getRecurrentAppointment()!=null){
                RecurrentAppointmentDTO recurrentAppointmentDTO = recurrenceService.getRecurrentAppointmentByID(shiftNote.getRecurrentAppointment().getRecurrentAppointmentID());
                timeSheet.setRecurrentAppointment(recurrentAppointmentDTO);
                AppointmentDTO appointmentDTO = appointmentService.getAppointmentById(recurrentAppointmentDTO.getAppointmentID());
                timeSheet.setAppointment(appointmentDTO);

                ClientDTO clientDTO = clientService.getClientById(appointmentDTO.getClientID());
                timeSheet.setClient(clientDTO);
            }
            if (clientID!=""){
                if (clientID.equalsIgnoreCase(timeSheet.getClient().getClientID())){
                    timeSheetList.add(timeSheet);
                }
            }else{
                timeSheetList.add(timeSheet);
            }

        }
        if (careGiverID.equalsIgnoreCase("all")){
            return timeSheetList;
        }else{
            return timeSheetList.stream().filter(timeSheetDTO -> timeSheetDTO.getEmployeeDTO().getEmployeeID().equalsIgnoreCase(careGiverID)).toList();
        }

    }

    public List<ShiftNoteDTO> getAllShiftNotesRelatedToCareGiver(String employeeID){
        Employee employee = employeeService.findOneEmployeeWithEntity(employeeID);
        return shiftNoteRepository.getAllShiftNotesByEmployee(employee).stream().map(this::toDTO).toList();

    }

    private ShiftNoteDTO toDTO(ShiftNotes shiftNotes) {
        ShiftNoteDTO shiftNoteDTO = new ShiftNoteDTO();
        shiftNoteDTO.setNoteID(shiftNotes.getNoteID());
        shiftNoteDTO.setTitle(shiftNotes.getTitle());
        shiftNoteDTO.setShiftStartDate(shiftNotes.getShiftStartDate());
        shiftNoteDTO.setShiftStartTime(shiftNotes.getShiftStartTime());
        shiftNoteDTO.setShiftEndDate(shiftNotes.getShiftEndDate());
        shiftNoteDTO.setShiftEndTime(shiftNotes.getShiftEndTime());
        shiftNoteDTO.setSystemShiftStartDate(shiftNotes.getSystemShiftStartDate());
        shiftNoteDTO.setSystemShiftEndDate(shiftNotes.getSystemShiftEndDate());
        shiftNoteDTO.setSystemShiftStartTime(shiftNotes.getSystemShiftStartTime());
        shiftNoteDTO.setSystemShiftEndTime(shiftNotes.getSystemShiftEndTime());
        shiftNoteDTO.setPaymentState(shiftNotes.getPaymentState());
        shiftNoteDTO.setRecurrentAppointmentID(
                shiftNotes.getRecurrentAppointment() != null ?
                        shiftNotes.getRecurrentAppointment().getRecurrentAppointmentID() : null
        );
        shiftNoteDTO.setTotalWorkHrs(shiftNotes.getWorkHours());
        shiftNoteDTO.setNotes(shiftNotes.getNotes());
        shiftNoteDTO.setComments(shiftNotes.getComments());
        shiftNoteDTO.setEmployeeID(
                shiftNotes.getEmployee() != null ? shiftNotes.getEmployee().getEmployeeID() : null
        );
        shiftNoteDTO.setCareGiverID(
                shiftNotes.getCareGiver() != null ? shiftNotes.getCareGiver().getCareGiverID() : null
        );
        shiftNoteDTO.setState(shiftNotes.getState());
        if (shiftNotes.getClient()!=null){
            shiftNoteDTO.setClientID(shiftNotes.getClient().getClientID());
        }

        // Convert associated documents

        List<ShiftNoteDocumentDTO> documentDTOs = shiftDocumentService.getDocumentsRelatedToShiftNoteID(shiftNotes.getNoteID()).stream().map(doc -> {
            ShiftNoteDocumentDTO documentDTO = new ShiftNoteDocumentDTO();
            documentDTO.setDocumentId(doc.getDocumentId());
            documentDTO.setDocumentName(doc.getDocumentName());
            documentDTO.setDocumentLocation(doc.getDocumentLocation());
            documentDTO.setShiftNoteID(doc.getShiftNoteID());
            return documentDTO;
        }).toList();

        shiftNoteDTO.setDocuments(documentDTOs);
        return shiftNoteDTO;
    }

    public String updateShiftNotePaymentStatus(String id,String state,String comment){
        ShiftNotes existingShiftNote = shiftNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ShiftNote not found"));
        existingShiftNote.setPaymentState(state);
        existingShiftNote.setComments(comment);
        shiftNoteRepository.save(existingShiftNote);
        return "Updated state successfully";
    }

    public ShiftNoteDTO updateShiftNote(String noteID, ShiftNoteDTO shiftNoteDTO, List<MultipartFile> files) {
        // Fetch the existing ShiftNote entity
        ShiftNotes existingShiftNote = shiftNoteRepository.findById(noteID)
                .orElseThrow(() -> new RuntimeException("ShiftNote not found"));

        // Update fields in the existing entity
        updateEntityFromDTO(shiftNoteDTO, existingShiftNote);

        // Save the updated note
        ShiftNotes updatedNote = shiftNoteRepository.save(existingShiftNote);

        try {
            // Handle associated documents
            List<ShiftNoteDocumentDTO> documents = shiftNoteDTO.getDocuments();

            // Remove documents that are no longer present
            if (documents != null) {
                for (ShiftNoteDocumentDTO document : documents) {
                    ShiftNoteDocumentDTO existingDocument = shiftDocumentService.getDocumentById(document.getDocumentId()).getBody();
                    if (existingDocument != null && existingDocument.getShiftNoteID() != null) {
                        File fileToDelete = new File(existingDocument.getDocumentLocation());
                        if (fileToDelete.exists() && !fileToDelete.delete()) {
                            throw new RuntimeException("Failed to delete the file: " + fileToDelete.getName());
                        }else{
                            shiftDocumentService.deleteDocument(existingDocument.getDocumentId());
                        }
                    }
                }
            }
            System.out.println("process came here without issue");
            System.out.println("files are here  "+files);

            // Add new files
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (file != null) {
                        String originalFileName = file.getOriginalFilename();
                        String fileExtension = "";
                        if (originalFileName != null && originalFileName.contains(".")) {
                            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                        }

                        File directory = new File(DIRECTORY);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }

                        File destinationFile = new File(DIRECTORY, noteID + "_" + System.currentTimeMillis() + fileExtension);
                        if (destinationFile.exists()) {
                            if (!destinationFile.delete()) {
                                throw new RuntimeException("Failed to delete the existing file: " + destinationFile.getName());
                            }
                        }

                        file.transferTo(destinationFile);

                        ShiftNoteDocumentDTO newDocument = new ShiftNoteDocumentDTO();
                        newDocument.setShiftNoteID(noteID);
                        newDocument.setDocumentId(Micro_utils.idGenerator("shiftNoteDocuments", null));
                        newDocument.setDocumentName(originalFileName);
                        newDocument.setDocumentLocation(destinationFile.getAbsolutePath());

                        shiftDocumentService.saveDocument(newDocument);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("error in the context "+e);
            throw new RuntimeException("Error while updating shift note and documents", e);
        }
        // Convert the updated entity back to DTO and return
        return toDTO(updatedNote);
    }

    private void updateEntityFromDTO(ShiftNoteDTO dto, ShiftNotes entity) {
        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getShiftStartDate() != null) {
            entity.setShiftStartDate(dto.getShiftStartDate());
        }
        if (dto.getShiftStartTime() != null) {
            entity.setShiftStartTime(dto.getShiftStartTime());
        }
        if (dto.getShiftEndDate() != null) {
            entity.setShiftEndDate(dto.getShiftEndDate());
        }
        if (dto.getShiftEndTime() != null) {
            entity.setShiftEndTime(dto.getShiftEndTime());
        }
        if (dto.getNotes() != null) {
            entity.setNotes(dto.getNotes());
        }
        if (dto.getComments() != null) {
            entity.setComments(dto.getComments());
        }
        if (dto.getState() != null) {
            entity.setState(dto.getState());
        }
        entity.setWorkHours(dto.getTotalWorkHrs());
        if (dto.getClientID()!=null && !dto.getClientID().isEmpty()){
            Client client = clientService.getClientByIdWithEntitiy(dto.getClientID());
            entity.setClient(client);
        }
        if (dto.getCareGiverID()!=null && !dto.getCareGiverID().isEmpty()){
            CareGiver careGiver = careGiverService.getCareGiverByIdWithEntity(dto.getCareGiverID());
            entity.setCareGiver(careGiver);
        }
        entity.setWorkHours(dto.getTotalWorkHrs());
    }


    private ShiftNotes toEntity(ShiftNoteDTO shiftNoteDTO) {
        ShiftNotes shiftNotes = new ShiftNotes();
        shiftNotes.setNoteID(shiftNoteDTO.getNoteID());
        shiftNotes.setTitle(shiftNoteDTO.getTitle());
        shiftNotes.setShiftStartDate(shiftNoteDTO.getShiftStartDate());
        shiftNotes.setShiftStartTime(shiftNoteDTO.getShiftStartTime());
        shiftNotes.setShiftEndDate(shiftNoteDTO.getShiftEndDate());
        shiftNotes.setShiftEndTime(shiftNoteDTO.getShiftEndTime());
        shiftNotes.setSystemShiftStartDate(shiftNoteDTO.getSystemShiftStartDate());
        shiftNotes.setSystemShiftEndDate(shiftNoteDTO.getSystemShiftEndDate());
        shiftNotes.setSystemShiftStartTime(shiftNoteDTO.getSystemShiftStartTime());
        shiftNotes.setSystemShiftEndTime(shiftNoteDTO.getSystemShiftEndTime());
        shiftNotes.setNotes(shiftNoteDTO.getNotes());
        shiftNotes.setComments(shiftNoteDTO.getComments());
        shiftNotes.setState(shiftNoteDTO.getState());
        shiftNotes.setWorkHours(shiftNoteDTO.getTotalWorkHrs());
        shiftNotes.setPaymentState(shiftNoteDTO.getPaymentState());

        // Employee and CareGiver need to be set externally if required
        return shiftNotes;
    }




    public ShiftNotes getShiftNotesByIdWithEntity(String shiftNoteID) {
        Optional<ShiftNotes> note = shiftNoteRepository.findById(shiftNoteID);
        if(note.isPresent()){
            return note.get();
        }else{
            throw new RuntimeException();
        }
    }

    public long getCompletedShiftNoteCount(String employeeID){
        Employee employee = employeeService.findOneEmployeeWithEntity(employeeID);
        return shiftNoteRepository.countByStateCompletedAndEmployee(employee);
    }


    public ShiftNoteDTO getShiftNote(String shiftNoteID){
        Optional<ShiftNotes> note = shiftNoteRepository.findById(shiftNoteID);
        if(note.isPresent()){
            return toDTO(note.get());
        }else{
            throw new RuntimeException();
        }
    }
}
