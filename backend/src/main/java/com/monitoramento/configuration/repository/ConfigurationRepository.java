package com.monitoramento.configuration.repository;

import com.monitoramento.configuration.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    Optional<Configuration> findTopByOrderByIdDesc();
}
