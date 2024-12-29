package com.skycareSolutions.skyCarePortal.scheduler;

import com.skycareSolutions.skyCarePortal.dto.AppointmentDTO;
import com.skycareSolutions.skyCarePortal.dto.CareGiverDTO;
import com.skycareSolutions.skyCarePortal.dto.JobAssignerDTO;
import com.skycareSolutions.skyCarePortal.dto.RecurrentAppointmentDTO;
import com.skycareSolutions.skyCarePortal.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class AppointmentReminderMail {

    @Autowired
    private RecurrenceService recurrenceService;

    @Autowired
    private JobAssignerService jobAssignerService;

    @Autowired
    private CareGiverService careGiverService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AppointmentService appointmentService;

    @Value("${app.external.service.frontendUrl}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at 12:00 AM
    public void sendAppointmentReminders(){

        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        // Fetch appointments within the date range (today)
        List<RecurrentAppointmentDTO> recurrentAppointmentList =
                recurrenceService.getAppointmentsWithinGivenDateRange(formattedDate, formattedDate);

        for (RecurrentAppointmentDTO reApp:recurrentAppointmentList){
            List<JobAssignerDTO> jobAssignerDTOList = jobAssignerService.findJobAssignerByRecurrentID(reApp.getRecurrentAppointmentID());
            for (JobAssignerDTO assinger:jobAssignerDTOList){
                if (assinger.getAcceptanceType().equalsIgnoreCase("Accepted")){
                    AppointmentDTO appointmentDTO = appointmentService.getAppointmentById(assinger.getAppointmentID());
                    CareGiverDTO careGiverDTO = careGiverService.getCareGiverById(assinger.getCareGiverID()).getBody();
                    CompletableFuture.runAsync(()->emailService.generateAppointmentReminderEmail(careGiverDTO.getEmployee().getFirstName(),
                            careGiverDTO.getEmployee().getEmail(),formattedDate,appointmentDTO.getTitle(),frontendUrl));
                }
            }
        }



    }
}
