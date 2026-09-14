package com.monitoramento.camera.controller;

import com.monitoramento.camera.dto.CameraRequest;
import com.monitoramento.camera.dto.CameraResponse;
import com.monitoramento.camera.service.CameraService;
import com.monitoramento.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cameras")
@RequiredArgsConstructor
@Tag(name = "Câmeras", description = "Operações relacionadas ao cadastro e manipulação de câmeras")
public class CameraController {

    private final CameraService cameraService;

    @GetMapping
    @Operation(summary = "Listar câmeras")
    public ResponseEntity<ApiResponse<List<CameraResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(cameraService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar câmera por id")
    public ResponseEntity<ApiResponse<CameraResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(cameraService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Criar câmera")
    public ResponseEntity<ApiResponse<CameraResponse>> create(@Valid @RequestBody CameraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(cameraService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar câmera")
    public ResponseEntity<ApiResponse<CameraResponse>> update(@PathVariable Long id, @Valid @RequestBody CameraRequest request) {
        return ResponseEntity.ok(ApiResponse.success(cameraService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir câmera")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        cameraService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Câmera removida com sucesso.", null));
    }
}
