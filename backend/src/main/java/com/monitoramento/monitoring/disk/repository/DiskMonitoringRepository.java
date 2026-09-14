package com.monitoramento.monitoring.disk.repository;

import com.monitoramento.monitoring.disk.entity.DiskMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiskMonitoringRepository extends JpaRepository<DiskMonitoring, Long> {
}
