package com.monitoramento.monitoring.camera.controller;

import com.monitoramento.monitoring.camera.entity.CameraMonitoring;
import com.monitoramento.monitoring.camera.service.CameraMonitoringService;
import com.monitoramento.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
@Tag(name = "Monitoramento de câmeras", description = "Verificação de disponibilidade das câmeras")
public class CameraMonitoringController {

    private final CameraMonitoringService cameraMonitoringService;

    @GetMapping("/cameras/{cameraId}/latest")
    @Operation(summary = "Buscar último monitoramento de uma câmera")
    public ResponseEntity<ApiResponse<CameraMonitoring>> getLatest(@PathVariable Long cameraId) {
        List<CameraMonitoring> history = cameraMonitoringService.findHistory(cameraId);
        CameraMonitoring latest = history.isEmpty() ? null : history.get(0);
        return ResponseEntity.ok(ApiResponse.success(latest));
    }

    @GetMapping("/cameras/{cameraId}/history")
    @Operation(summary = "Buscar histórico de monitoramento de uma câmera")
    public ResponseEntity<ApiResponse<List<CameraMonitoring>>> getHistory(@PathVariable Long cameraId) {
        return ResponseEntity.ok(ApiResponse.success(cameraMonitoringService.findHistory(cameraId)));
    }

    @PostMapping("/cameras/{cameraId}/check")
    @Operation(summary = "Executar verificação de uma câmera")
    public ResponseEntity<ApiResponse<CameraMonitoring>> executeCheck(@PathVariable Long cameraId) {
        return ResponseEntity.ok(ApiResponse.success(cameraMonitoringService.executeCheck(cameraId)));
    }
}
