package com.monitoramento.monitoring.disk.controller;

import com.monitoramento.monitoring.disk.entity.DiskMonitoring;
import com.monitoramento.monitoring.disk.service.DiskMonitoringService;
import com.monitoramento.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
@Tag(name = "Monitoramento de disco", description = "Consulta de métricas de uso do disco")
public class DiskMonitoringController {

    private final DiskMonitoringService diskMonitoringService;

    @GetMapping("/disk/latest")
    @Operation(summary = "Buscar último monitoramento do disco")
    public ResponseEntity<ApiResponse<DiskMonitoring>> getLatest() {
        List<DiskMonitoring> history = diskMonitoringService.findHistory();
        DiskMonitoring latest = history.isEmpty() ? null : history.get(history.size() - 1);
        return ResponseEntity.ok(ApiResponse.success(latest));
    }

    @GetMapping("/disk/history")
    @Operation(summary = "Buscar histórico do disco")
    public ResponseEntity<ApiResponse<List<DiskMonitoring>>> getHistory() {
        return ResponseEntity.ok(ApiResponse.success(diskMonitoringService.findHistory()));
    }

    @PostMapping("/disk/check")
    @Operation(summary = "Executar verificação do disco")
    public ResponseEntity<ApiResponse<DiskMonitoring>> executeCheck() {
        return ResponseEntity.ok(ApiResponse.success(diskMonitoringService.executeCheck()));
    }
}
