package com.monitoramento.monitoring.internet.service;

import com.monitoramento.alert.entity.Alert;
import com.monitoramento.alert.repository.AlertRepository;
import com.monitoramento.configuration.entity.Configuration;
import com.monitoramento.configuration.internet.InternetConfigurationService;
import com.monitoramento.monitoring.internet.entity.InternetMonitoring;
import com.monitoramento.monitoring.internet.repository.InternetMonitoringRepository;
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
public class InternetMonitoringService {

    private final InternetMonitoringRepository internetMonitoringRepository;
    private final InternetConfigurationService internetConfigurationService;
    private final AlertRepository alertRepository;

    public InternetMonitoring executeCheck() {
        Configuration configuration = internetConfigurationService.getRequiredConfiguration();

        String host = configuration.getInternetIpTest() != null ? configuration.getInternetIpTest() : "8.8.8.8";
        int timeout = configuration.getInternetTimeoutMs() != null ? configuration.getInternetTimeoutMs() : 5000;

        long start = System.currentTimeMillis();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, 80), timeout);
            long elapsed = System.currentTimeMillis() - start;

            InternetMonitoring monitoring = InternetMonitoring.builder()
                    .executedAt(LocalDateTime.now())
                    .status("ONLINE")
                    .responseTimeMs(elapsed)
                    .errorMessage(null)
                    .build();

            InternetMonitoring saved = internetMonitoringRepository.save(monitoring);
            evaluateAlert(saved, true);
            return saved;
        } catch (IOException ex) {
            long elapsed = System.currentTimeMillis() - start;

            InternetMonitoring monitoring = InternetMonitoring.builder()
                    .executedAt(LocalDateTime.now())
                    .status("OFFLINE")
                    .responseTimeMs(elapsed)
                    .errorMessage(ex.getMessage())
                    .build();

            InternetMonitoring saved = internetMonitoringRepository.save(monitoring);
            evaluateAlert(saved, false);
            return saved;
        }
    }

    public List<InternetMonitoring> findHistory() {
        return internetMonitoringRepository.findAll();
    }

    private void evaluateAlert(InternetMonitoring monitoring, boolean healthy) {
        MonitoringState currentState = MonitoringStateMachine.evaluateCurrentState(
                MonitoringState.ONLINE,
                healthy,
                "INTERNET"
        );

        if (currentState == MonitoringState.ALERT_OPEN) {
            alertRepository.findTopByTypeAndStatusOrderByCreatedAtDesc("INTERNET", "OPEN")
                    .orElseGet(() -> {
                        Alert alert = Alert.builder()
                                .type("INTERNET")
                                .severity("HIGH")
                                .status("OPEN")
                                .message("Internet indisponível: " + monitoring.getErrorMessage())
                                .build();
                        return alertRepository.save(alert);
                    });
            return;
        }

        if (currentState == MonitoringState.RECOVERED) {
            alertRepository.findTopByTypeAndStatusOrderByCreatedAtDesc("INTERNET", "OPEN")
                    .ifPresent(alert -> {
                        alert.setStatus("RESOLVED");
                        alert.setResolvedAt(LocalDateTime.now());
                        alertRepository.save(alert);
                    });
        }
    }
}
