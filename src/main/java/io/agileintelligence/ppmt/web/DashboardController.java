package io.agileintelligence.ppmt.web;

import io.agileintelligence.ppmt.service.DashboardService;
import io.agileintelligence.ppmt.web.dto.DashboardSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public DashboardSummary summary() {
        return dashboardService.getSummary();
    }
}