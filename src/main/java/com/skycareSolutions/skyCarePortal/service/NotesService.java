package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.NoteDocumentsDTO;
import com.skycareSolutions.skyCarePortal.dto.NotesDTO;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.NotesRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotesService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "notes").toString();


    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private NotesDocumentService notesDocumentService;

    public NotesDTO createNote(NotesDTO notesDTO, List<MultipartFile> files) {
        String lastNoteID = notesRepository.getLastIdOfNotes();
        String newNoteID = Micro_utils.idGenerator("notes",lastNoteID);
        notesDTO.setNoteID(newNoteID);
        Notes note = convertToEntity(notesDTO);
        Notes savedNote = notesRepository.save(note);

        try {
            if (!files.isEmpty()){
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
                        File destinationFile = new File(DIRECTORY, newNoteID + fileExtension);
                        file.transferTo(destinationFile);
                        NoteDocumentsDTO docs  = new NoteDocumentsDTO();
                        docs.setNoteID(newNoteID);
                        docs.setDocID(originalFileName+new Date().toString());
                        docs.setDocument(destinationFile.getAbsolutePath());

                        notesDocumentService.saveNoteDocument(docs);

                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return convertToDTO(savedNote);
    }

    public NotesDTO updateNote(String noteID, NotesDTO notesDTO) {
        Notes existingNote = notesRepository.findById(noteID)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // Manually update the existing entity using the DTO
        updateEntityFromDTO(notesDTO, existingNote);

        Notes updatedNote = notesRepository.save(existingNote);
        return convertToDTO(updatedNote);
    }

    public List<NotesDTO> getAllNotes() {
        List<Notes> notes = notesRepository.getAllNotes();
        List<NotesDTO> notesDTO = new ArrayList<>();
        for (Notes note:notes){
            notesDTO.add(convertToDTO(note));
        }
        return notesDTO;
    }

    public NotesDTO getNoteById(String noteID) {
        Notes note = notesRepository.findById(noteID)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        return convertToDTO(note);
    }

    public List<NotesDTO> getNotesByClientId(String clientID) {
        return notesRepository.findByClientID(clientID)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteNoteById(String noteID) {
        notesRepository.deleteById(noteID);
    }

    // Manual mapping: NotesDTO -> Notes
    private Notes convertToEntity(NotesDTO dto) {
        Notes note = new Notes();
        note.setNoteID(dto.getNoteID());
        note.setTitle(dto.getTitle());
        note.setCreateDate(dto.getCreateDate());
        note.setShiftStartTime(dto.getShiftStartTime());
        note.setShiftEndTime(dto.getShiftEndTime());
        note.setDescription(dto.getDescription());
        note.setEffectiveDate(dto.getEffectiveDate());
        note.setSharedGroup(dto.getSharedGroup());
        note.setCreatedBy(dto.getCreatedBy());
        note.setNoteType(dto.getNoteType());

        // Set relationships manually
        if (dto.getAppointmentID() != null) {
            Appointment appointment = new Appointment();
            appointment.setAppointmentID(dto.getAppointmentID());
            note.setAppointmentID(appointment);
        }

        if (dto.getClientID() != null) {
            Client client = new Client();
            client.setClientID(dto.getClientID());
            note.setClientID(client);
        }

        if (dto.getTaskID() != null) {
            Task task = new Task();
            task.setTaskID(dto.getTaskID());
            note.setTaskID(task);
        }

        if (dto.getCareplanID() != null) {
            CarePlan carePlan = new CarePlan();
            carePlan.setCareplanID(dto.getCareplanID());
            note.setCareplanID(carePlan);
        }

        return note;
    }

    // Manual mapping: Notes -> NotesDTO
    private NotesDTO convertToDTO(Notes note) {
        NotesDTO dto = new NotesDTO();
        dto.setNoteID(note.getNoteID());
        dto.setTitle(note.getTitle());
        dto.setCreateDate(note.getCreateDate());
        dto.setShiftStartTime(note.getShiftStartTime());
        dto.setShiftEndTime(note.getShiftEndTime());
        dto.setDescription(note.getDescription());
        dto.setEffectiveDate(note.getEffectiveDate());
        dto.setSharedGroup(note.getSharedGroup());
        dto.setCreatedBy(note.getCreatedBy());
        dto.setNoteType(note.getNoteType());

        // Set relationships manually
        if (note.getAppointmentID() != null) {
            dto.setAppointmentID(note.getAppointmentID().getAppointmentID());
        }

        if (note.getClientID() != null) {
            dto.setClientID(note.getClientID().getClientID());
        }

        if (note.getTaskID() != null) {
            dto.setTaskID(note.getTaskID().getTaskID());
        }

        if (note.getCareplanID() != null) {
            dto.setCareplanID(note.getCareplanID().getCareplanID());
        }

        return dto;
    }

    // Optional: Helper method for updating an existing entity
    private void updateEntityFromDTO(NotesDTO dto, Notes note) {
        note.setTitle(dto.getTitle());
        note.setCreateDate(dto.getCreateDate());
        note.setShiftStartTime(dto.getShiftStartTime());
        note.setShiftEndTime(dto.getShiftEndTime());
        note.setDescription(dto.getDescription());
        note.setEffectiveDate(dto.getEffectiveDate());
        note.setSharedGroup(dto.getSharedGroup());
        note.setCreatedBy(dto.getCreatedBy());
        note.setNoteType(dto.getNoteType());

        // Update relationships manually
        if (dto.getAppointmentID() != null) {
            Appointment appointment = new Appointment();
            appointment.setAppointmentID(dto.getAppointmentID());
            note.setAppointmentID(appointment);
        } else {
            note.setAppointmentID(null);
        }

        if (dto.getClientID() != null) {
            Client client = new Client();
            client.setClientID(dto.getClientID());
            note.setClientID(client);
        } else {
            note.setClientID(null);
        }

        if (dto.getTaskID() != null) {
            Task task = new Task();
            task.setTaskID(dto.getTaskID());
            note.setTaskID(task);
        } else {
            note.setTaskID(null);
        }

        if (dto.getCareplanID() != null) {
            CarePlan carePlan = new CarePlan();
            carePlan.setCareplanID(dto.getCareplanID());
            note.setCareplanID(carePlan);
        } else {
            note.setCareplanID(null);
        }
    }
}
