package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.model.CareGiver;
import com.skycareSolutions.skyCarePortal.model.JobAssigner;
import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.repository.CareGiverRepository;
import com.skycareSolutions.skyCarePortal.repository.EmployeeRepository;
import com.skycareSolutions.skyCarePortal.repository.JobAssignerRepository;
import com.skycareSolutions.skyCarePortal.utils.TemporaryHiddenClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RegisterAccessService {

    @Autowired
    private CareGiverRepository careGiverRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JobAssignerRepository jobAssignerRepository;

    public boolean isAdminRole(String role) {
        return role != null && (role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("Super Admin"));
    }

    public boolean isCareGiverRole(String role) {
        return role != null && role.equalsIgnoreCase("CareGiver");
    }

    public void assertAdmin(String requesterRole) {
        if (!isAdminRole(requesterRole)) {
            throw new RuntimeException("Admin access required");
        }
    }

    public Set<String> getAssignedClientIdsForEmployee(String employeeId) {
        Set<String> clientIds = new HashSet<>();
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            return clientIds;
        }
        CareGiver careGiver = careGiverRepository.getCareGiverByEmployee(employee);
        if (careGiver == null) {
            return clientIds;
        }
        List<JobAssigner> jobs = jobAssignerRepository.findJobsAssignedToCareGiver(careGiver);
        for (JobAssigner job : jobs) {
            Appointment appointment = job.getAppointment();
            if (appointment != null && appointment.getClient() != null
                    && !TemporaryHiddenClients.isHidden(appointment.getClient().getClientID())) {
                clientIds.add(appointment.getClient().getClientID());
            }
        }
        return clientIds;
    }

    public void assertClientAccess(String requesterRole, String requesterEmployeeId, String clientId) {
        if (TemporaryHiddenClients.isHidden(clientId)) {
            throw new RuntimeException("Client not found");
        }
        if (isAdminRole(requesterRole)) {
            return;
        }
        if (clientId == null || clientId.isBlank()) {
            throw new RuntimeException("Client is required");
        }
        Set<String> allowed = getAssignedClientIdsForEmployee(requesterEmployeeId);
        if (!allowed.contains(clientId)) {
            throw new RuntimeException("You are not assigned to this client");
        }
    }
}
