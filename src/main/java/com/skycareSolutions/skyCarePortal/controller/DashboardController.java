package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.CareGiverDashboardDTO;
import com.skycareSolutions.skyCarePortal.dto.DashboardDTO;
import com.skycareSolutions.skyCarePortal.dto.MobileDashboardDTO;
import com.skycareSolutions.skyCarePortal.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/admin")
    public DashboardDTO getDashboardStats() {
        return dashboardService.getAdminDashboardStats();
    }

    @GetMapping("/careGiver/{id}")
    public CareGiverDashboardDTO getCareGiverStats(@PathVariable String id){
        return dashboardService.getCareGiverStats(id);
    }

    @GetMapping("/mobile/{id}")
    public MobileDashboardDTO getMobileDashboard(@PathVariable String id){
        return dashboardService.getMobileDashboardComponents(id);
    }

}
