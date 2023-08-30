package no.cantara.tools.stats.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class DailyStatus implements Serializable {
    private UserSessionStatus userSessionStatus;
    private UserApplicationStatistics userApplicationStatistics;

}
