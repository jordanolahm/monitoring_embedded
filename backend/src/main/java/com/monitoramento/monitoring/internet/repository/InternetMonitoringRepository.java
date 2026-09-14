package com.monitoramento.monitoring.internet.repository;

import com.monitoramento.monitoring.internet.entity.InternetMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternetMonitoringRepository extends JpaRepository<InternetMonitoring, Long> {
}
