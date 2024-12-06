package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.AppointmentAttachmentsDTO;
import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.model.AppointmentAttachments;
import com.skycareSolutions.skyCarePortal.model.AppointmentAttachmentsId;
import com.skycareSolutions.skyCarePortal.model.Client;
import com.skycareSolutions.skyCarePortal.repository.AppointmentAttachmentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentAttachmentsService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "AppointmentAttachments").toString();


    @Autowired
     private AppointmentAttachmentsRepository appointmentAttachmentsRepository;

    @Autowired
    @Lazy
    private AppointmentService appointmentService;


     public AppointmentAttachmentsDTO createAttachements(AppointmentAttachmentsDTO attachments, List<MultipartFile> files){
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
                 File destinationFile = new File(DIRECTORY, attachments.getAppointmentID()+"_"+originalFileName+ fileExtension);
                 file.transferTo(destinationFile);
                 attachments.setDocument(destinationFile.getAbsolutePath());
                 attachments.setDocumentID(originalFileName);

                 appointmentAttachmentsRepository.save(toEntity(attachments));

             } catch (Exception e) {
                 throw new RuntimeException(e);
             }
         }
         return new AppointmentAttachmentsDTO();

     }

     public List<AppointmentAttachmentsDTO> getAttachmentsbyAppointmentID(String appointmentID){
            Appointment appointment = appointmentService.getAppointmentByIdWithEntity(appointmentID);
            List<AppointmentAttachments> attachments = appointmentAttachmentsRepository.getAppointmentAttachmentByAppointmentID(appointment);
            List<AppointmentAttachmentsDTO> attachmentList = new ArrayList<>();
            for (AppointmentAttachments att:attachments){
                attachmentList.add(toDTO(att));
            }
            return attachmentList;
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

    private AppointmentAttachmentsDTO toDTO(AppointmentAttachments attachments){
         AppointmentAttachmentsDTO attachmentsDTO = new AppointmentAttachmentsDTO();
         attachmentsDTO.setAppointmentID(attachments.getAppointment().getAppointmentID());
         attachmentsDTO.setDocumentID(attachments.getId().getDocumentID());
         attachmentsDTO.setDocument(attachments.getDocument());
         return attachmentsDTO;
    }


}
