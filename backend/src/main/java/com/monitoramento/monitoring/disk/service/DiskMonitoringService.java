package com.monitoramento.monitoring.disk.service;

import com.monitoramento.alert.entity.Alert;
import com.monitoramento.alert.repository.AlertRepository;
import com.monitoramento.configuration.disk.DiskConfigurationService;
import com.monitoramento.configuration.entity.Configuration;
import com.monitoramento.monitoring.disk.entity.DiskMonitoring;
import com.monitoramento.monitoring.disk.repository.DiskMonitoringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiskMonitoringService {

    private final DiskMonitoringRepository diskMonitoringRepository;
    private final DiskConfigurationService diskConfigurationService;
    private final AlertRepository alertRepository;

    public DiskMonitoring executeCheck() {
        File file = new File("/");
        long total = file.getTotalSpace();
        long free = file.getFreeSpace();
        long used = total - free;
        double percentage = total > 0 ? (used * 100.0) / total : 0.0;

        DiskMonitoring monitoring = DiskMonitoring.builder()
                .executedAt(LocalDateTime.now())
                .totalBytes(total)
                .usedBytes(used)
                .freeBytes(free)
                .usagePercentage(percentage)
                .build();

        DiskMonitoring saved = diskMonitoringRepository.save(monitoring);
        evaluateAlert(saved);
        return saved;
    }

    public List<DiskMonitoring> findHistory() {
        return diskMonitoringRepository.findAll();
    }

    private void evaluateAlert(DiskMonitoring monitoring) {
        Configuration configuration = diskConfigurationService.getCurrentConfiguration();

        Integer threshold = configuration != null && configuration.getDiskAlertThresholdPercent() != null
                ? configuration.getDiskAlertThresholdPercent()
                : 85;

        boolean exceedsThreshold = monitoring.getUsagePercentage() >= threshold;
        boolean hasOpenAlert = alertRepository.findTopByTypeAndStatusOrderByCreatedAtDesc("DISK", "OPEN").isPresent();

        if (exceedsThreshold && !hasOpenAlert) {
            Alert alert = Alert.builder()
                    .type("DISK")
                    .severity("MEDIUM")
                    .status("OPEN")
                    .message("Uso do disco acima do limite configurado: " + monitoring.getUsagePercentage() + "%")
                    .build();
            alertRepository.save(alert);
            return;
        }

        if (!exceedsThreshold && hasOpenAlert) {
            alertRepository.findTopByTypeAndStatusOrderByCreatedAtDesc("DISK", "OPEN")
                    .ifPresent(alert -> {
                        alert.setStatus("RESOLVED");
                        alert.setResolvedAt(LocalDateTime.now());
                        alertRepository.save(alert);
                    });
        }
    }
}
