package com.monitoramento.camera.service;

import com.monitoramento.camera.dto.CameraRequest;
import com.monitoramento.camera.dto.CameraResponse;
import com.monitoramento.camera.entity.Camera;
import com.monitoramento.camera.repository.CameraRepository;
import com.monitoramento.shared.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CameraService {

    private final CameraRepository cameraRepository;
    private final PasswordEncoder passwordEncoder;

    public List<CameraResponse> findAll() {
        return cameraRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CameraResponse findById(Long id) {
        Camera camera = cameraRepository.findById(id)
                .orElseThrow(() -> new ApiException("Câmera não encontrada."));
        return toResponse(camera);
    }

    public CameraResponse create(CameraRequest request) {
        Camera camera = Camera.builder()
                .name(request.getName())
                .description(request.getDescription())
                .ip(request.getIp())
                .httpPort(request.getHttpPort())
                .rtspPort(request.getRtspPort())
                .username(request.getUsername())
                .passwordEncrypted(request.getPassword() != null ? passwordEncoder.encode(request.getPassword()) : null)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return toResponse(cameraRepository.save(camera));
    }

    public CameraResponse update(Long id, CameraRequest request) {
        Camera camera = cameraRepository.findById(id)
                .orElseThrow(() -> new ApiException("Câmera não encontrada."));

        camera.setName(request.getName());
        camera.setDescription(request.getDescription());
        camera.setIp(request.getIp());
        camera.setHttpPort(request.getHttpPort());
        camera.setRtspPort(request.getRtspPort());
        camera.setUsername(request.getUsername());
        camera.setActive(request.getActive() != null ? request.getActive() : camera.getActive());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            camera.setPasswordEncrypted(passwordEncoder.encode(request.getPassword()));
        }

        return toResponse(cameraRepository.save(camera));
    }

    public void delete(Long id) {
        Camera camera = cameraRepository.findById(id)
                .orElseThrow(() -> new ApiException("Câmera não encontrada."));
        cameraRepository.delete(camera);
    }

    private CameraResponse toResponse(Camera camera) {
        return CameraResponse.builder()
                .id(camera.getId())
                .name(camera.getName())
                .description(camera.getDescription())
                .ip(camera.getIp())
                .httpPort(camera.getHttpPort())
                .rtspPort(camera.getRtspPort())
                .username(camera.getUsername())
                .active(camera.getActive())
                .createdAt(camera.getCreatedAt())
                .updatedAt(camera.getUpdatedAt())
                .build();
    }
}
