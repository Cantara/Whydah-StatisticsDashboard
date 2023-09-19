package no.cantara.tools.stats.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DailyStatus implements Serializable {
    private UserSessionStatus userSessionStatus;
    private List<UserApplicationStatistics> userApplicationStatistics;
    ActivityStatistics activityStatistics = new ActivityStatistics();

}
