package no.cantara.tools.stats.domain;


import no.cantara.tools.stats.status.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


public class DailyStatus implements Serializable {
    public static final Logger log= LoggerFactory.getLogger(DailyStatus.class);

    private UserSessionStatus userSessionStatus;
    private List<UserApplicationStatistics> userApplicationStatistics;
    private ActivityStatistics activityStatistics = new ActivityStatistics();

    private TreeMap<String, HourlyStatus> hourlyStatusTreeMap = new TreeMap<>();


    public UserSessionStatus getUserSessionStatus() {
        if (userSessionStatus==null){
            userSessionStatus=new UserSessionStatus();
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
        if (activityStatistics==null){
            activityStatistics=new ActivityStatistics();
        }
        if (activityStatistics!=null && activityStatistics.getActivities().getUserSessions()!=null && activityStatistics.getActivities().getUserSessions().size() > 200){
            ActivityCollection activities = this.activityStatistics.getActivities();
            List<UserSessionActivity> userSessions = activities.getUserSessions();
            List<UserSessionActivity> myLastUserSessions = userSessions.subList(userSessions.size()-100, userSessions.size());
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
            //List<UserSessionActivity> userSessionActivity = activities.getUserSessions();
            //userSessionActivity.addAll(userSessions);  //TODO  move into HourlyStatus
            //userSessionActivity=userSessions;
            List<UserSessionActivity> myLastUserSessions = userSessions.subList(userSessions.size()-100, userSessions.size());

            activities.setUserSessions(myLastUserSessions);

        } catch (Exception e) {
            log.error("Exception in trying to populate userSessions", e);
        }
    }

    public void setHourlyStatus(String hourstring,HourlyStatus hourlyStatus){
        this.hourlyStatusTreeMap.put(hourstring,hourlyStatus);

    }

    public  TreeMap<String, HourlyStatus> getHourlyStatusTreeMap() {
        if (hourlyStatusTreeMap==null){
            hourlyStatusTreeMap = new TreeMap<>();
        }
        return hourlyStatusTreeMap;
    }

    public  void setHourlyStatusTreeMap(TreeMap<String, HourlyStatus> hourlyStatusTreeMap) {
        this.hourlyStatusTreeMap = hourlyStatusTreeMap;
    }
}
