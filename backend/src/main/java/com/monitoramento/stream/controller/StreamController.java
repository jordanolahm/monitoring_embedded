package com.monitoramento.stream.controller;

import com.monitoramento.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Stream", description = "Operações de stream de câmera")
public class StreamController {

    @PostMapping("/cameras/{id}/stream/start")
    @Operation(summary = "Iniciar stream da câmera")
    public ResponseEntity<ApiResponse<Map<String, String>>> startStream(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "message", "Stream iniciado para a câmera " + id,
                "url", "/api/cameras/" + id + "/stream"
        )));
    }

    @GetMapping("/cameras/{id}/stream")
    @Operation(summary = "Obter URL de stream da câmera")
    public ResponseEntity<ApiResponse<Map<String, String>>> getStream(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "cameraId", String.valueOf(id),
                "streamUrl", "rtsp://camera.local/stream/" + id
        )));
    }

    @PostMapping("/cameras/{id}/stream/stop")
    @Operation(summary = "Encerrar stream da câmera")
    public ResponseEntity<ApiResponse<Map<String, String>>> stopStream(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "message", "Stream encerrado para a câmera " + id
        )));
    }
}
