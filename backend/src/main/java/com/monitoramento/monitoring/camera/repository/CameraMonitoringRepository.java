package com.monitoramento.monitoring.camera.repository;

import com.monitoramento.monitoring.camera.entity.CameraMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CameraMonitoringRepository extends JpaRepository<CameraMonitoring, Long> {

    List<CameraMonitoring> findByCameraIdOrderByExecutedAtDesc(Long cameraId);
}
