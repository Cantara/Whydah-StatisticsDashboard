package no.cantara.tools.stats.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatistics {
	
    private String prefix = "ALL";
    private String activityName="userSession";
    private long startTime=0;
    private long endTime=0;
    private ActivityCollection activities;
}
