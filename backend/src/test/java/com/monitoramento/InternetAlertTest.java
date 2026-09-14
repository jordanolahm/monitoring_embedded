package com.monitoramento;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.monitoramento.alert.repository.AlertRepository;
import com.monitoramento.monitoring.internet.repository.InternetMonitoringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InternetAlertTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private InternetMonitoringRepository internetMonitoringRepository;

    @BeforeEach
    void clearMonitoringData() {
        alertRepository.deleteAll();
        internetMonitoringRepository.deleteAll();
    }

    @Test
    void createsInternetAlertWhenConnectivityCheckFails() throws Exception {
        String authorization = "Bearer " + authenticate();
        ObjectNode originalConfiguration = currentConfiguration(authorization);
        ObjectNode failingConfiguration = originalConfiguration.deepCopy();
        failingConfiguration.put("internetIpTest", "invalid.monitoramento.test");
        failingConfiguration.put("internetTimeoutMs", 1000);

        try {
            updateConfiguration(authorization, failingConfiguration);

            mockMvc.perform(post("/api/monitoring/internet/check")
                            .header("Authorization", authorization))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.status").value("OFFLINE"))
                    .andExpect(jsonPath("$.data.errorMessage").isNotEmpty());

            mockMvc.perform(get("/api/alerts")
                            .header("Authorization", authorization))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].type").value("INTERNET"))
                    .andExpect(jsonPath("$.data[0].status").value("OPEN"))
                    .andExpect(jsonPath("$.data[0].message", startsWith("Internet indisponível:")));
        } finally {
            updateConfiguration(authorization, originalConfiguration);
        }
    }

    private String authenticate() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data")
                .path("token")
                .asText();
    }

    private ObjectNode currentConfiguration(String authorization) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/configurations")
                        .header("Authorization", authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        JsonNode configuration = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data");
        return ((ObjectNode) configuration).deepCopy();
    }

    private void updateConfiguration(String authorization, ObjectNode configuration) throws Exception {
        mockMvc.perform(put("/api/configurations")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(configuration)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}