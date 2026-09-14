package com.monitoramento.configuration.service;

import com.monitoramento.configuration.entity.Configuration;
import com.monitoramento.configuration.repository.ConfigurationRepository;
import com.monitoramento.shared.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public Configuration getCurrentConfiguration() {
        return configurationRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new ApiException("Configuração não encontrada."));
    }

    public Configuration save(Configuration configuration) {
        return configurationRepository.save(configuration);
    }
}
