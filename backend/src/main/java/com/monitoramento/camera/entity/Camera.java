package com.monitoramento.camera.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "camera")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Camera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String ip;

    @Column(name = "http_port", nullable = false)
    private Integer httpPort;

    @Column(name = "rtsp_port", nullable = false)
    private Integer rtspPort;

    @Column(name = "username")
    private String username;

    @Column(name = "password_encrypted")
    private String passwordEncrypted;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
