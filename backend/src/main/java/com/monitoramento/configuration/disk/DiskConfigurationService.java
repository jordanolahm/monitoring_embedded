package com.monitoramento.configuration.disk;

import com.monitoramento.configuration.entity.Configuration;
import com.monitoramento.configuration.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiskConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public Configuration getCurrentConfiguration() {
        return configurationRepository.findTopByOrderByIdDesc().orElse(null);
    }
}