package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.AdminInsightsDashboardDTO;
import com.skycareSolutions.skyCarePortal.dto.CareGiverDashboardDTO;
import com.skycareSolutions.skyCarePortal.dto.DashboardDTO;
import com.skycareSolutions.skyCarePortal.dto.MobileAdminDashboardDTO;
import com.skycareSolutions.skyCarePortal.dto.MobileDashboardDTO;
import com.skycareSolutions.skyCarePortal.service.AdminDashboardInsightsService;
import com.skycareSolutions.skyCarePortal.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@CrossOrigin("*")
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AdminDashboardInsightsService adminDashboardInsightsService;

    @GetMapping("/admin")
    public DashboardDTO getDashboardStats() {
        return dashboardService.getAdminDashboardStats();
    }

    @GetMapping("/admin/insights")
    public AdminInsightsDashboardDTO getAdminInsightsDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return adminDashboardInsightsService.getInsights(from, to);
    }

    @GetMapping("/careGiver/{id}")
    public CareGiverDashboardDTO getCareGiverStats(@PathVariable String id){
        return dashboardService.getCareGiverStats(id);
    }

    @GetMapping("/mobile/{id}")
    public MobileDashboardDTO getMobileDashboard(@PathVariable String id){
        return dashboardService.getMobileDashboardComponents(id);
    }

    @GetMapping("/mobile/client/{id}")
    public MobileDashboardDTO getClientDashboardStatus(@PathVariable String id){
        return dashboardService.getClientMobileDashboardComponents(id);
    }

    @GetMapping("/mobile/admin")
    public MobileAdminDashboardDTO getMobileAdminDashboardStats() {

        return dashboardService.getMobileAdminDashboardStats();
    }





}
