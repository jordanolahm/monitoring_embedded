package com.monitoramento.dashboard.controller;

import com.monitoramento.alert.entity.Alert;
import com.monitoramento.alert.repository.AlertRepository;
import com.monitoramento.camera.repository.CameraRepository;
import com.monitoramento.dashboard.dto.DashboardSummaryDto;
import com.monitoramento.monitoring.disk.entity.DiskMonitoring;
import com.monitoramento.monitoring.disk.repository.DiskMonitoringRepository;
import com.monitoramento.monitoring.internet.entity.InternetMonitoring;
import com.monitoramento.monitoring.internet.repository.InternetMonitoringRepository;
import com.monitoramento.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dados agregados para o painel do sistema")
public class DashboardController {

    private final CameraRepository cameraRepository;
    private final InternetMonitoringRepository internetMonitoringRepository;
    private final DiskMonitoringRepository diskMonitoringRepository;
    private final AlertRepository alertRepository;

    @GetMapping("/dashboard/summary")
    @Operation(summary = "Resumo do dashboard")
    public ResponseEntity<ApiResponse<DashboardSummaryDto>> summary() {
        InternetMonitoring latestInternet = internetMonitoringRepository.findAll().stream()
                .reduce((first, second) -> second)
                .orElse(null);

        DiskMonitoring latestDisk = diskMonitoringRepository.findAll().stream()
                .reduce((first, second) -> second)
                .orElse(null);

        List<Alert> alerts = alertRepository.findAllByOrderByCreatedAtDesc().stream().limit(5).toList();

        List<Map<String, Object>> recentAlerts = alerts.stream().map(alert -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", alert.getId());
            item.put("type", alert.getType());
            item.put("severity", alert.getSeverity());
            item.put("status", alert.getStatus());
            item.put("message", alert.getMessage());
            item.put("createdAt", alert.getCreatedAt());
            return item;
        }).toList();

        Map<String, Object> diskSummary = new HashMap<>();
        if (latestDisk != null) {
            diskSummary.put("id", latestDisk.getId());
            diskSummary.put("totalBytes", latestDisk.getTotalBytes());
            diskSummary.put("usedBytes", latestDisk.getUsedBytes());
            diskSummary.put("freeBytes", latestDisk.getFreeBytes());
            diskSummary.put("usagePercentage", latestDisk.getUsagePercentage());
            diskSummary.put("executedAt", latestDisk.getExecutedAt());
        }

        DashboardSummaryDto dto = DashboardSummaryDto.builder()
                .totalCameras(cameraRepository.count())
                .activeCameras((long) cameraRepository.findByActiveTrue().size())
                .internetStatus(latestInternet != null ? latestInternet.getStatus() : "UNKNOWN")
                .diskLatest(diskSummary)
                .recentAlerts(recentAlerts)
                .build();

        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}
