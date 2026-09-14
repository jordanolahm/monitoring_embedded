package com.monitoramento.monitoring.disk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "monitoramento_disco")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiskMonitoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Column(name = "total_bytes", nullable = false)
    private Long totalBytes;

    @Column(name = "used_bytes", nullable = false)
    private Long usedBytes;

    @Column(name = "free_bytes", nullable = false)
    private Long freeBytes;

    @Column(name = "usage_percentage", nullable = false)
    private Double usagePercentage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
