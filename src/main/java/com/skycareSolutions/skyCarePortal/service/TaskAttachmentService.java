package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.AppointmentAttachmentsDTO;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.AppointmentAttachmentsRepository;
import com.skycareSolutions.skyCarePortal.repository.TaskAttachmentRepository;
import com.skycareSolutions.skyCarePortal.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

@Service
public class TaskAttachmentService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "Task_Attachments").toString();


    @Autowired
    private TaskAttachmentRepository taskAttachmentsRepository;

    @Autowired
    @Lazy
    private AppointmentService appointmentService;

    @Autowired
    @Lazy
    private TaskService taskService;


    public void createAttachements(List<MultipartFile> files,String taskID){
        for(MultipartFile file:files){
            try {
                System.out.println("looping through files "+ file.getOriginalFilename());
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
                File destinationFile = new File(DIRECTORY, taskID+"_"+originalFileName+ fileExtension);
                file.transferTo(destinationFile);

                TaskAttachmentID id = new TaskAttachmentID(taskID,originalFileName);
                TaskAttachements attachmentDoc = new TaskAttachements();
                attachmentDoc.setId(id);
                attachmentDoc.setTask(taskService.getTaskByIdWithEntity(taskID));
                attachmentDoc.setDocument(destinationFile.getAbsolutePath());
                taskAttachmentsRepository.save(attachmentDoc);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    private AppointmentAttachments toEntity(AppointmentAttachmentsDTO attachmentsDTO) {
        // Check if the appointment exists
        Appointment appointment = appointmentService.getAppointmentByIdWithEntity(attachmentsDTO.getAppointmentID());
        if (appointment == null) {
            throw new RuntimeException("Appointment with ID " + attachmentsDTO.getAppointmentID() + " does not exist.");
        }

        AppointmentAttachments attachments = new AppointmentAttachments();
        attachments.setAppointment(appointment); // Set the existing appointment
        attachments.setDocument(attachmentsDTO.getDocument());
        attachments.setId(new AppointmentAttachmentsId(attachmentsDTO.getAppointmentID(),attachmentsDTO.getDocumentID()));

        return attachments;
    }
}
