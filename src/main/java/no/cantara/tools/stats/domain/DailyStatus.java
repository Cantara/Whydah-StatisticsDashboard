package no.cantara.tools.stats.domain;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DailyStatus implements Serializable {
    public static final Logger log = LoggerFactory.getLogger(DailyStatus.class);

    // Added: Get timezone from config or use default
    private static final ZoneId APP_ZONE_ID = ZoneId.of(
        System.getProperty("app.stats.timezone", "Europe/Oslo")
    );
    private static final DateTimeFormatter simpleHourFormatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd:HH").withZone(APP_ZONE_ID);

    private UserSessionStatus userSessionStatus;
    private List<UserApplicationStatistics> userApplicationStatistics;
    private ActivityStatistics activityStatistics = new ActivityStatistics();
    private TreeMap<String, HourlyStatus> hourlyStatusTreeMap = new TreeMap<>();

    public UserSessionStatus getUserSessionStatus() {
        if (userSessionStatus == null) {
            userSessionStatus = new UserSessionStatus();
            userSessionStatus.setLast_updated(ZonedDateTime.now(APP_ZONE_ID)); // FIXED
        }
        return userSessionStatus;
    }

    public void setUserSessionStatus(UserSessionStatus userSessionStatus) {
        this.userSessionStatus = userSessionStatus;
    }

    public List<UserApplicationStatistics> getUserApplicationStatistics() {
        return userApplicationStatistics;
    }

    public void setUserApplicationStatistics(List<UserApplicationStatistics> userApplicationStatistics) {
        this.userApplicationStatistics = userApplicationStatistics;
    }

    public ActivityStatistics getActivityStatistics() {
        if (activityStatistics == null) {
            activityStatistics = new ActivityStatistics();
        }
        if (activityStatistics != null && activityStatistics.getActivities().getUserSessions() != null 
            && activityStatistics.getActivities().getUserSessions().size() > 200) {
            ActivityCollection activities = this.activityStatistics.getActivities();
            List<UserSessionActivity> userSessions = activities.getUserSessions();
            List<UserSessionActivity> myLastUserSessions = userSessions.subList(userSessions.size() - 100, userSessions.size());
            activities.setUserSessions(myLastUserSessions);
        }
        return activityStatistics;
    }

    public void setActivityStatistics(ActivityStatistics activityStatistics) {
        this.activityStatistics = activityStatistics;
    }

    public void addActivityStatistics(List<UserSessionActivity> userSessions) {
        try {
            if (this.activityStatistics.getActivities() == null) {
                this.activityStatistics.setActivities(new ActivityCollection());
            }
            if (this.activityStatistics.getActivities().getUserSessions() == null) {
                ActivityCollection activities = this.activityStatistics.getActivities();
                List<UserSessionActivity> userSessionActivity = activities.getUserSessions();
                userSessionActivity = new ArrayList<>();
            }
            ActivityCollection activities = this.activityStatistics.getActivities();
            List<UserSessionActivity> myLastUserSessions = userSessions.subList(userSessions.size() - 100, userSessions.size());
            activities.setUserSessions(myLastUserSessions);
        } catch (Exception e) {
            log.error("Exception in trying to populate userSessions", e);
        }
    }

    public void setHourlyStatus(String hourstring, HourlyStatus hourlyStatus) {
        this.hourlyStatusTreeMap.put(hourstring, hourlyStatus);
    }

    public TreeMap<String, HourlyStatus> getHourlyStatusTreeMap() {
        if (hourlyStatusTreeMap == null) {
            hourlyStatusTreeMap = new TreeMap<>();
        }
        fillHourlyTreeMap(hourlyStatusTreeMap);
        return hourlyStatusTreeMap;
    }

    public void setHourlyStatusTreeMap(TreeMap<String, HourlyStatus> hourlyStatusTreeMap) {
        this.hourlyStatusTreeMap = hourlyStatusTreeMap;
        fillHourlyTreeMap(hourlyStatusTreeMap);
    }

    private void fillHourlyTreeMap(TreeMap treeMap) {
        // FIXED: Use ZonedDateTime with timezone
        String currentHour = simpleHourFormatter.format(ZonedDateTime.now(APP_ZONE_ID));
        int length = currentHour.length();

        for (int n = 0; n < 24; n++) {
            String loopHour;
            if (n < 10) {
                loopHour = currentHour.substring(0, length - 2) + "0" + n;
            } else {
                loopHour = currentHour.substring(0, length - 2) + n;
            }
            treeMap.computeIfAbsent(loopHour, k -> new HourlyStatus()); // FIXED: Should be HourlyStatus, not TreeMap
        }
    }
}