package com.monitoramento.configuration.controller;

import com.monitoramento.configuration.entity.Configuration;
import com.monitoramento.configuration.service.ConfigurationService;
import com.monitoramento.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configurations")
@RequiredArgsConstructor
@Tag(name = "Configuração", description = "Configurações do sistema de monitoramento")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping
    @Operation(summary = "Buscar configuração atual")
    public ResponseEntity<ApiResponse<Configuration>> getConfiguration() {
        return ResponseEntity.ok(ApiResponse.success(configurationService.getCurrentConfiguration()));
    }

    @PutMapping
    @Operation(summary = "Atualizar configuração")
    public ResponseEntity<ApiResponse<Configuration>> update(@RequestBody Configuration configuration) {
        return ResponseEntity.ok(ApiResponse.success(configurationService.save(configuration)));
    }
}
