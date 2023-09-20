package no.cantara.tools.stats.domain;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatistics implements Serializable {
	
    private String prefix = "ALL";
    private String activityName="userSession";
    private long startTime=0;
    private long endTime=0;
    private ActivityCollection activities=new ActivityCollection();

    public ActivityCollection getActivities() {
        if (activities==null){
            activities=new ActivityCollection();
        }
        return activities;
    }
}
