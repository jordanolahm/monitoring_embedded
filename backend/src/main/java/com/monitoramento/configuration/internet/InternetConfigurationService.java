package com.monitoramento.configuration.internet;

import com.monitoramento.configuration.entity.Configuration;
import com.monitoramento.configuration.repository.ConfigurationRepository;
import com.monitoramento.shared.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternetConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public Configuration getRequiredConfiguration() {
        return configurationRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new ApiException("Configuração não encontrada."));
    }

    public Configuration getCurrentConfiguration() {
        return configurationRepository.findTopByOrderByIdDesc().orElse(null);
    }
}