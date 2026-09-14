package com.monitoramento.alert.controller;

import com.monitoramento.alert.entity.Alert;
import com.monitoramento.alert.repository.AlertRepository;
import com.monitoramento.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Alertas", description = "Consulta de alertas do sistema")
public class AlertController {

    private final AlertRepository alertRepository;

    @GetMapping("/alerts")
    @Operation(summary = "Listar alertas")
    public ResponseEntity<ApiResponse<List<Alert>>> getAlerts() {
        return ResponseEntity.ok(ApiResponse.success(alertRepository.findAllByOrderByCreatedAtDesc()));
    }
}
