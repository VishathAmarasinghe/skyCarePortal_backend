package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.AppointmentType;
import com.skycareSolutions.skyCarePortal.model.CareGiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    @Lazy
    private AppointmentService appointmentService;

    @Autowired
    @Lazy
    private RecurrenceService recurrenceService;

    @Autowired
    @Lazy
    private AppointmentTypeService appointmentTypeService;

    @Autowired
    @Lazy
    private EmployeeService employeeService;

    @Autowired
    @Lazy
    private ClientService clientService;

    @Autowired
    @Lazy
    private CareGiverService careGiverService;

    @Autowired
    @Lazy
    private JobAssignerService jobAssignerService;

    @Autowired
    @Lazy
    private ShiftNoteService shiftNoteService;

    public DashboardDTO getAdminDashboardStats(){
        DashboardDTO dashboardDTO  = new DashboardDTO();

        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusDays(30);

        // Define the format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Format the date
        String formattedDate = currentDate.format(formatter);
        String formattedEndDate = endDate.format(formatter);

        List<AppointmentCalenderDTO> calender = appointmentService.getAllEventsRelatedToDateRange(formattedDate,formattedDate);
        List<RecurrentAppointmentDTO> Todayappointments = recurrenceService.getAppointmentsWithinGivenDateRange(formattedDate,formattedDate);

        List<RecurrentAppointmentDTO> appointments = recurrenceService.getAppointmentsWithinGivenDateRange(formattedDate,formattedEndDate);

        List<RecurrentAppointmentDTO> filteredAppointments =appointments.stream().filter(ap-> !ap.isCancelled()).toList();

        Map<String, Long> appointmentCountByDate = new HashMap<>();

// Loop through each day in the range
        for (LocalDate date = currentDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String formattedCurrentDate = date.format(formatter);

            // Filter appointments for the current day
            LocalDate finalDate = date;
            long count = filteredAppointments.stream()
                    .filter(appointment -> appointment.getStartDate().equals(finalDate))
                    .count();

            // Add to the map
            appointmentCountByDate.put(formattedCurrentDate, count);
        }

        int completeCount = 0;
        for (RecurrentAppointmentDTO rd:Todayappointments){
            if (rd.getStatus().equalsIgnoreCase("Completed")){
                completeCount++;
            }
        }

        dashboardDTO.setTodayCompletedCount(completeCount);
        dashboardDTO.setTodayTotalCount(calender.size());

        dashboardDTO.setTwoWeekAppointmentCount(appointmentCountByDate);
        dashboardDTO.setTodayAppointments(calender);

        dashboardDTO.setAdminCount(employeeService.getEmployeesByRole("Admin").size());
        dashboardDTO.setServiceCount(appointmentService.getAllAppointments().size());
        dashboardDTO.setStaffCount(employeeService.getEmployeesByRole("CareGiver").size());
        dashboardDTO.setClientCount(clientService.getAllClients().size());

        Map<String, Integer> appointmentCountByType = new HashMap<>();

        List<AppointmentTypeDTO> appointmentTypeList = appointmentTypeService.getAllAppointmentTypes();
        for (AppointmentTypeDTO ap:appointmentTypeList){
            int count = appointmentService.getAppointmentCountBasedOnAppointmentType(ap.getAppointmentTypeID());
            appointmentCountByType.put(ap.getName(),count);
        }
        dashboardDTO.setAppointmentCountByType(appointmentCountByType);
        return dashboardDTO;
    }

    public CareGiverDashboardDTO getCareGiverStats(String employeeID){
        CareGiverDashboardDTO careGiverDashboardDTO = new CareGiverDashboardDTO();

        CareGiverDTO careGiver = careGiverService.getCareGiverByEmployee(employeeID);

        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusDays(30);

        // Define the format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Format the date
        String formattedDate = currentDate.format(formatter);
        String formattedEndDate = endDate.format(formatter);

        List<AppointmentCalenderDTO> calender = appointmentService.getAllEventsRelatedToDateRange(formattedDate,formattedDate);
        List<RecurrentAppointmentDTO> Todayappointments = recurrenceService.getAppointmentsWithinGivenDateRange(formattedDate,formattedDate);

        List<RecurrentAppointmentDTO> appointments = recurrenceService.getAppointmentsWithinGivenDateRange(formattedDate,formattedEndDate);
        List<RecurrentAppointmentDTO> filteredNotCanceledAppointments = appointments.stream().filter(ap-> !ap.isCancelled()).toList();

        List<RecurrentAppointmentDTO> filteredAppointments = new ArrayList<>();
        for (RecurrentAppointmentDTO reApp:filteredNotCanceledAppointments){
            List<JobAssignerDTO> jobList = jobAssignerService.findJobAssignerByRecurrentID(reApp.getRecurrentAppointmentID());
            for (JobAssignerDTO job:jobList){
                if (job.getCareGiverID().equalsIgnoreCase(careGiver.getCareGiverID())){
                    filteredAppointments.add(reApp);
                    break;
                }
            }
        }

        Map<String, Long> appointmentCountByDate = new HashMap<>();

// Loop through each day in the range
        for (LocalDate date = currentDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String formattedCurrentDate = date.format(formatter);

            // Filter appointments for the current day
            LocalDate finalDate = date;
            long count = filteredAppointments.stream()
                    .filter(appointment -> appointment.getStartDate().equals(finalDate))
                    .count();

            // Add to the map
            appointmentCountByDate.put(formattedCurrentDate, count);
        }
        careGiverDashboardDTO.setTwoWeekAppointmentCount(appointmentCountByDate);
        return careGiverDashboardDTO;
    }

    public MobileDashboardDTO getMobileDashboardComponents(String userID){
        MobileDashboardDTO result = new MobileDashboardDTO();
        result.setCompletedAppointmentCount(shiftNoteService.getCompletedShiftNoteCount(userID));
        List<PendingAppointmentDTO> appointments=appointmentService.getPendingAppointmentsRelatedToUser(userID);
        result.setPendingAppointmentCount(appointments.size());

        return result;
    }
}
