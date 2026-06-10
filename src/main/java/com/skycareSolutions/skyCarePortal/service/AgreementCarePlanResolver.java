package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.model.CarePlan;
import com.skycareSolutions.skyCarePortal.model.CarePlanSupportSchedule;
import com.skycareSolutions.skyCarePortal.model.CareGiver;
import com.skycareSolutions.skyCarePortal.model.Client;
import com.skycareSolutions.skyCarePortal.repository.CareGiverRepository;
import com.skycareSolutions.skyCarePortal.repository.CarePlanRepository;
import com.skycareSolutions.skyCarePortal.repository.CarePlanSupportScheduleRepository;
import com.skycareSolutions.skyCarePortal.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AgreementCarePlanResolver {

    @Autowired
    private CarePlanRepository carePlanRepository;

    @Autowired
    private CarePlanSupportScheduleRepository supportScheduleRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CareGiverRepository careGiverRepository;

    @Autowired
    private JobAssignerService jobAssignerService;

    public void populateCarePlanTokens(Map<String, String> tokens, String clientId, String careGiverId) {
        String resolvedClientId = resolveClientId(clientId, careGiverId);
        if (resolvedClientId == null) {
            populateEmptyCarePlanTokens(tokens);
            return;
        }
        Client client = clientRepository.findById(resolvedClientId).orElse(null);
        if (client == null) {
            populateEmptyCarePlanTokens(tokens);
            return;
        }
        CarePlan activePlan = carePlanRepository.findByClient(client).stream()
                .filter(cp -> cp.getStatus() != null && cp.getStatus().equalsIgnoreCase("Active"))
                .findFirst()
                .orElse(null);
        if (activePlan == null) {
            populateEmptyCarePlanTokens(tokens);
            return;
        }
        tokens.put("{{carePlan.id}}", safe(activePlan.getCareplanID()));
        tokens.put("{{carePlan.title}}", safe(activePlan.getTitle()));
        tokens.put("{{carePlan.startDate}}", safe(activePlan.getStartDate()));
        tokens.put("{{carePlan.endDate}}", safe(activePlan.getEndDate()));
        tokens.put("{{carePlan.status}}", safe(activePlan.getStatus()));
        tokens.put("{{carePlan.currentLivingSituation}}", safe(activePlan.getCurrentLivingSituation()));
        tokens.put("{{carePlan.expectedOutcomes}}", safe(activePlan.getExpectedOutcomes()));
        tokens.put("{{carePlan.riskManagement}}", safe(activePlan.getRiskManagement()));
        tokens.put("{{carePlan.supportsFundedCore}}", safe(activePlan.getSupportsFundedCore()));
        tokens.put("{{carePlan.supportsFundedCapacityBuilding}}", safe(activePlan.getSupportsFundedCapacityBuilding()));
        tokens.put("{{carePlan.supportsFundedCapital}}", safe(activePlan.getSupportsFundedCapital()));
        tokens.put("{{carePlan.billingBudgetNotes}}", safe(activePlan.getBillingBudgetNotes()));
        tokens.put("{{carePlan.supportScheduleTable}}", buildSupportScheduleTable(activePlan));
    }

    public void populateSampleCarePlanTokens(Map<String, String> tokens) {
        tokens.put("{{carePlan.id}}", "CP00001");
        tokens.put("{{carePlan.title}}", "Support Plan 2026");
        tokens.put("{{carePlan.startDate}}", "01 Jan 2026");
        tokens.put("{{carePlan.endDate}}", "31 Dec 2026");
        tokens.put("{{carePlan.status}}", "Active");
        tokens.put("{{carePlan.currentLivingSituation}}", "Lives independently with family support.");
        tokens.put("{{carePlan.expectedOutcomes}}", "Increased community participation and daily living skills.");
        tokens.put("{{carePlan.riskManagement}}", "Standard risk management protocols apply.");
        tokens.put("{{carePlan.supportsFundedCore}}", "Core supports as per NDIS plan.");
        tokens.put("{{carePlan.supportsFundedCapacityBuilding}}", "Capacity building supports.");
        tokens.put("{{carePlan.supportsFundedCapital}}", "");
        tokens.put("{{carePlan.billingBudgetNotes}}", "");
        tokens.put("{{carePlan.supportScheduleTable}}",
                "<table><thead><tr><th>Day</th><th>Support Type</th><th>Hours</th><th>Rate</th></tr></thead>"
                        + "<tbody><tr><td>Monday</td><td>Community Access</td><td>4</td><td>$65.00</td></tr></tbody></table>");
    }

    private String resolveClientId(String clientId, String careGiverId) {
        if (clientId != null && !clientId.isBlank()) {
            return clientId;
        }
        if (careGiverId != null && !careGiverId.isBlank()) {
            CareGiver careGiver = careGiverRepository.findById(careGiverId).orElse(null);
            if (careGiver != null && careGiver.getEmployee() != null) {
                List<String> clients = jobAssignerService.getAllClientIDListCareGiverAssociated(
                        careGiver.getEmployee().getEmployeeID());
                if (!clients.isEmpty()) {
                    return clients.get(0);
                }
            }
        }
        return null;
    }

    private String buildSupportScheduleTable(CarePlan carePlan) {
        List<CarePlanSupportSchedule> schedules = supportScheduleRepository.findAllByCarePlan(carePlan);
        if (schedules.isEmpty()) {
            return "";
        }
        StringBuilder html = new StringBuilder(
                "<table><thead><tr><th>Day</th><th>Support Type</th><th>Hours</th><th>Rate</th></tr></thead><tbody>");
        for (CarePlanSupportSchedule row : schedules) {
            html.append("<tr>")
                    .append("<td>").append(escape(safe(row.getDay()))).append("</td>")
                    .append("<td>").append(escape(safe(row.getSupportType()))).append("</td>")
                    .append("<td>").append(row.getHours()).append("</td>")
                    .append("<td>").append(row.getRate()).append("</td>")
                    .append("</tr>");
        }
        html.append("</tbody></table>");
        return html.toString();
    }

    private void populateEmptyCarePlanTokens(Map<String, String> tokens) {
        tokens.put("{{carePlan.id}}", "");
        tokens.put("{{carePlan.title}}", "");
        tokens.put("{{carePlan.startDate}}", "");
        tokens.put("{{carePlan.endDate}}", "");
        tokens.put("{{carePlan.status}}", "");
        tokens.put("{{carePlan.currentLivingSituation}}", "");
        tokens.put("{{carePlan.expectedOutcomes}}", "");
        tokens.put("{{carePlan.riskManagement}}", "");
        tokens.put("{{carePlan.supportsFundedCore}}", "");
        tokens.put("{{carePlan.supportsFundedCapacityBuilding}}", "");
        tokens.put("{{carePlan.supportsFundedCapital}}", "");
        tokens.put("{{carePlan.billingBudgetNotes}}", "");
        tokens.put("{{carePlan.supportScheduleTable}}", "");
    }

    private String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}
