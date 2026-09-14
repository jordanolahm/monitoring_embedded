package com.monitoramento.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {

    private Long totalCameras;
    private Long activeCameras;
    private String internetStatus;
    private Map<String, Object> diskLatest;
    private List<Map<String, Object>> recentAlerts;
}
