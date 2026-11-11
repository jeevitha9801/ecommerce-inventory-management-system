package com.jeevitha.inventorysystem.controller;

import com.jeevitha.inventorysystem.dto.DashboardStatsResponse;
import com.jeevitha.inventorysystem.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public DashboardStatsResponse getStats() {

        return dashboardService.getStats();
    }
}