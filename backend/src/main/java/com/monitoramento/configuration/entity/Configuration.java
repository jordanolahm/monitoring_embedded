package com.monitoramento.configuration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "configuracao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "internet_ip_test")
    private String internetIpTest;

    @Column(name = "internet_timeout_ms")
    private Integer internetTimeoutMs;

    @Column(name = "internet_period_seconds")
    private Integer internetPeriodSeconds;

    @Column(name = "disk_period_seconds")
    private Integer diskPeriodSeconds;

    @Column(name = "disk_alert_threshold_percent")
    private Integer diskAlertThresholdPercent;

    @Column(name = "camera_period_seconds")
    private Integer cameraPeriodSeconds;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
