package com.monitoramento.monitoring.scheduler;

import com.monitoramento.camera.entity.Camera;
import com.monitoramento.camera.repository.CameraRepository;
import com.monitoramento.configuration.camera.CameraConfigurationService;
import com.monitoramento.configuration.disk.DiskConfigurationService;
import com.monitoramento.configuration.entity.Configuration;
import com.monitoramento.configuration.internet.InternetConfigurationService;
import com.monitoramento.monitoring.camera.service.CameraMonitoringService;
import com.monitoramento.monitoring.disk.service.DiskMonitoringService;
import com.monitoramento.monitoring.internet.service.InternetMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonitoringScheduler {

    private final InternetMonitoringService internetMonitoringService;
    private final DiskMonitoringService diskMonitoringService;
    private final CameraMonitoringService cameraMonitoringService;
    private final CameraRepository cameraRepository;
    private final InternetConfigurationService internetConfigurationService;
    private final DiskConfigurationService diskConfigurationService;
    private final CameraConfigurationService cameraConfigurationService;

    @Scheduled(fixedDelayString = "${app.monitoring.internet.default-period-seconds:60}000")
    public void runInternetMonitoring() {
        try {
            Configuration configuration = internetConfigurationService.getCurrentConfiguration();
            if (configuration == null || configuration.getInternetPeriodSeconds() == null) {
                internetMonitoringService.executeCheck();
                return;
            }

            internetMonitoringService.executeCheck();
            log.info("Monitoramento de internet executado.");
        } catch (Exception ex) {
            log.error("Erro ao executar monitoramento de internet: {}", ex.getMessage(), ex);
        }
    }

    @Scheduled(fixedDelayString = "${app.monitoring.disk.default-period-seconds:120}000")
    public void runDiskMonitoring() {
        try {
            Configuration configuration = diskConfigurationService.getCurrentConfiguration();
            if (configuration == null || configuration.getDiskPeriodSeconds() == null) {
                diskMonitoringService.executeCheck();
                return;
            }

            diskMonitoringService.executeCheck();
            log.info("Monitoramento de disco executado.");
        } catch (Exception ex) {
            log.error("Erro ao executar monitoramento de disco: {}", ex.getMessage(), ex);
        }
    }

    @Scheduled(fixedDelayString = "${app.monitoring.camera.default-period-seconds:180}000")
    public void runCameraMonitoring() {
        try {
            Configuration configuration = cameraConfigurationService.getCurrentConfiguration();
            if (configuration == null || configuration.getCameraPeriodSeconds() == null) {
                return;
            }

            List<Camera> activeCameras = cameraRepository.findByActiveTrue();
            for (Camera camera : activeCameras) {
                cameraMonitoringService.executeCheck(camera.getId());
            }
            log.info("Monitoramento de câmeras executado para {} câmeras ativas.", activeCameras.size());
        } catch (Exception ex) {
            log.error("Erro ao executar monitoramento de câmeras: {}", ex.getMessage(), ex);
        }
    }
}
