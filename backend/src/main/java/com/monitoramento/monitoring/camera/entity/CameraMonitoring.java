package com.monitoramento.monitoring.camera.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "monitoramento_camera")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CameraMonitoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "camera_id", nullable = false)
    private Long cameraId;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Column(nullable = false)
    private String status;

    @Column(name = "ping_success", nullable = false)
    private Boolean pingSuccess;

    @Column(name = "ping_response_time_ms")
    private Long pingResponseTimeMs;

    @Column(name = "frame_capture_success", nullable = false)
    private Boolean frameCaptureSuccess;

    @Column(name = "frame_capture_time_ms")
    private Long frameCaptureTimeMs;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
