package com.skycareSolutions.skyCarePortal.scheduler;

import com.skycareSolutions.skyCarePortal.dto.CareGiverDTO;
import com.skycareSolutions.skyCarePortal.dto.CareGiverDocumentTypesDTO;
import com.skycareSolutions.skyCarePortal.dto.CareGiverDocumentsDTO;
import com.skycareSolutions.skyCarePortal.dto.EmployeeDTO;
import com.skycareSolutions.skyCarePortal.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
public class DocumentExpirationReminder {

    @Autowired
    private CareGiverDocumentService careGiverDocumentService;

    @Autowired
    private CareGiverDocumentTypesService careGiverDocumentTypesService;

    @Autowired
    private CareGiverService careGiverService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmailService emailService;

    @Value("${app.external.service.frontendUrl}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 8 * * ?") // Runs every day at 8 AM
    public void sendDocumentationExpirationReminders() {
        List<CareGiverDocumentsDTO> careGiverDocuments = careGiverDocumentService.getAllDocuments();
        Set<String> careGiverList = new HashSet<>();

        LocalDate today = LocalDate.now();

        for (CareGiverDocumentsDTO doc : careGiverDocuments) {
            if (doc.getExpDate() != null) {
                // Convert java.util.Date to java.time.LocalDate
                LocalDate expDate = doc.getExpDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                if (expDate.isEqual(today)) {
                    Optional<CareGiverDocumentTypesDTO> documentType = careGiverDocumentTypesService.getDocumentTypeById(doc.getDocumentTypeID());
                    if (documentType.isPresent() && documentType.get().getStatus().equalsIgnoreCase("Active")) {
                        careGiverList.add(doc.getCareGiverID());
                    }
                }
            }
        }
        for (String careGiverID:careGiverList){
            CareGiverDTO careGiverDTO = careGiverService.getCareGiverById(careGiverID).getBody();
            if (careGiverDTO.getEmployee().getStatus().equalsIgnoreCase("Activated")){
                CompletableFuture.runAsync(()->emailService.generateDocumentsExpiredEmail(careGiverDTO.getEmployee().getFirstName(),frontendUrl,careGiverDTO.getEmployee().getEmail()));
            }
        }
    }
}
