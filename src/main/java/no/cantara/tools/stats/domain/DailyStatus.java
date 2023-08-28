package no.cantara.tools.stats.domain;

import lombok.Data;

@Data
public class DailyStatus {
    private UserSessionStatus userSessionStatus;
    private UserApplicationStatistics userApplicationStatistics;

}
