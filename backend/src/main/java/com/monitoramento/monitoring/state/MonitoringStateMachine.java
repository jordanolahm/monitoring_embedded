package com.monitoramento.monitoring.state;

public final class MonitoringStateMachine {

    private MonitoringStateMachine() {
    }

    public static MonitoringState evaluateCurrentState(MonitoringState previousState, boolean healthy, String resourceType) {
        if (healthy) {
            if (previousState == MonitoringState.ALERT_OPEN || previousState == MonitoringState.OFFLINE) {
                return MonitoringState.RECOVERED;
            }
            return MonitoringState.ONLINE;
        }

        if (previousState == MonitoringState.ONLINE || previousState == MonitoringState.RECOVERED) {
            return MonitoringState.ALERT_OPEN;
        }

        return MonitoringState.OFFLINE;
    }
}
