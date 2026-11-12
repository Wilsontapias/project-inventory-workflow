package com.tdea.backend.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tdea.backend.api.dto.dashboard.DashboardSummaryResponse;
import com.tdea.backend.service.DashboardService;

/** Endpoint REST con indicadores agregados para el dashboard. */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Inyecta el servicio de métricas del dashboard.
     *
     * @param dashboardService caso de uso de indicadores
     */
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Obtiene el resumen calculado de inventario y reabastecimiento.
     *
     * @return indicadores derivados de los datos guardados en H2
     */
    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }
}
