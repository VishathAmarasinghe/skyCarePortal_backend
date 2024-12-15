package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.AppointmentCalenderDTO;
import com.skycareSolutions.skyCarePortal.dto.DashboardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    @Lazy
    private AppointmentService appointmentService;

    public DashboardDTO getAdminDashboardStats(){
        DashboardDTO dashboardDTO  = new DashboardDTO();

        LocalDate currentDate = LocalDate.now();

        // Define the format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Format the date
        String formattedDate = currentDate.format(formatter);

        List<AppointmentCalenderDTO> calender = appointmentService.getAllEventsRelatedToDateRange(formattedDate,formattedDate);

        dashboardDTO.setTodayAppointments(calender);
        dashboardDTO.setAdminCount(4);
        dashboardDTO.setServiceCount(10);
        dashboardDTO.setStaffCount(3);
        dashboardDTO.setClientCount(1);
        return dashboardDTO;
    }
}
