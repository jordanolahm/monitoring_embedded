package com.monitoramento.monitoring.camera.service;

import com.monitoramento.alert.entity.Alert;
import com.monitoramento.alert.repository.AlertRepository;
import com.monitoramento.camera.entity.Camera;
import com.monitoramento.camera.repository.CameraRepository;
import com.monitoramento.configuration.camera.CameraConfigurationService;
import com.monitoramento.configuration.entity.Configuration;
import com.monitoramento.monitoring.camera.entity.CameraMonitoring;
import com.monitoramento.monitoring.camera.repository.CameraMonitoringRepository;
import com.monitoramento.shared.exception.ApiException;
import com.monitoramento.monitoring.state.MonitoringState;
import com.monitoramento.monitoring.state.MonitoringStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CameraMonitoringService {

    private final CameraRepository cameraRepository;
    private final CameraMonitoringRepository cameraMonitoringRepository;
    private final AlertRepository alertRepository;
    private final CameraConfigurationService cameraConfigurationService;

    public CameraMonitoring executeCheck(Long cameraId) {
        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new ApiException("Câmera não encontrada."));

        Configuration config = cameraConfigurationService.getRequiredConfiguration();

        boolean pingSuccess = pingCheck(camera.getIp(), config.getInternetTimeoutMs() != null ? config.getInternetTimeoutMs() : 2000);
        boolean frameSuccess = false;
        String errorMessage = null;

        if (!pingSuccess) {
            errorMessage = "Falha no ping da câmera.";
        } else {
            frameSuccess = rtspFrameCheck(camera);
            if (!frameSuccess) {
                errorMessage = "Falha ao capturar frame RTSP.";
            }
        }

        CameraMonitoring monitoring = CameraMonitoring.builder()
                .cameraId(cameraId)
                .executedAt(LocalDateTime.now())
                .status(pingSuccess && frameSuccess ? "ONLINE" : "OFFLINE")
                .pingSuccess(pingSuccess)
                .pingResponseTimeMs(pingSuccess ? 100L : null)
                .frameCaptureSuccess(frameSuccess)
                .frameCaptureTimeMs(frameSuccess ? 150L : null)
                .errorMessage(errorMessage)
                .build();

        CameraMonitoring saved = cameraMonitoringRepository.save(monitoring);
        evaluateAlert(cameraId, saved);
        return saved;
    }

    public List<CameraMonitoring> findHistory(Long cameraId) {
        return cameraMonitoringRepository.findByCameraIdOrderByExecutedAtDesc(cameraId);
    }

    private boolean pingCheck(String ip, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, 80), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean rtspFrameCheck(Camera camera) {
        try {
            String rtspUrl = "rtsp://" + camera.getUsername() + "@" + camera.getIp() + ":" + camera.getRtspPort() + "/stream";
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-v", "error",
                    "-rtsp_transport", "tcp",
                    "-i", rtspUrl,
                    "-frames:v", "1",
                    "-f", "null",
                    "-"
            );
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void evaluateAlert(Long cameraId, CameraMonitoring monitoring) {
        MonitoringState actualState = MonitoringStateMachine.evaluateCurrentState(
                MonitoringState.ONLINE,
                "ONLINE".equals(monitoring.getStatus()),
                "CAMERA"
        );

        if (actualState == MonitoringState.ALERT_OPEN) {
            alertRepository.findTopByCameraIdAndStatusOrderByCreatedAtDesc(cameraId, "OPEN")
                    .orElseGet(() -> {
                        Alert alert = Alert.builder()
                                .type("CAMERA")
                                .severity("HIGH")
                                .status("OPEN")
                                .message("Câmera offline: " + monitoring.getErrorMessage())
                                .cameraId(cameraId)
                                .build();
                        return alertRepository.save(alert);
                    });
            return;
        }

        if (actualState == MonitoringState.RECOVERED) {
            alertRepository.findTopByCameraIdAndStatusOrderByCreatedAtDesc(cameraId, "OPEN")
                    .ifPresent(alert -> {
                        alert.setStatus("RESOLVED");
                        alert.setResolvedAt(LocalDateTime.now());
                        alertRepository.save(alert);
                    });
        }
    }
}
