package com.monitoramento.monitoring.internet.controller;

import com.monitoramento.monitoring.internet.entity.InternetMonitoring;
import com.monitoramento.monitoring.internet.service.InternetMonitoringService;
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
@Tag(name = "Monitoramento de internet", description = "Operações de verificação de conectividade")
public class InternetMonitoringController {

    private final InternetMonitoringService internetMonitoringService;

    @GetMapping("/internet/latest")
    @Operation(summary = "Buscar último monitoramento de internet")
    public ResponseEntity<ApiResponse<InternetMonitoring>> getLatest() {
        List<InternetMonitoring> history = internetMonitoringService.findHistory();
        InternetMonitoring latest = history.isEmpty() ? null : history.get(history.size() - 1);
        return ResponseEntity.ok(ApiResponse.success(latest));
    }

    @GetMapping("/internet/history")
    @Operation(summary = "Buscar histórico de internet")
    public ResponseEntity<ApiResponse<List<InternetMonitoring>>> getHistory() {
        return ResponseEntity.ok(ApiResponse.success(internetMonitoringService.findHistory()));
    }

    @PostMapping("/internet/check")
    @Operation(summary = "Executar verificação de internet")
    public ResponseEntity<ApiResponse<InternetMonitoring>> executeCheck() {
        return ResponseEntity.ok(ApiResponse.success(internetMonitoringService.executeCheck()));
    }
}
