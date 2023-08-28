package no.cantara.tools.stats.status;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import no.cantara.tools.stats.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import no.cantara.config.ApplicationProperties;
import no.cantara.tools.stats.exception.AppExceptionCode;

public class StatusService {

    public static final Logger logger = LoggerFactory.getLogger(StatusService.class);
    String report_service;
    String uib_health_service;

    public static final String dateformat = "yyyy-MM-dd";

    public static final DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern(dateformat);
	public static final SimpleDateFormat simpleDateFormatter = new SimpleDateFormat(dateformat);
    private UserSessionStatusCache lastUpdatedStatusCache = new UserSessionStatusCache();
    private UserSessionStatus recentStatus = null;
    private Map<String, DailyStatus> dailyStatusMap = new HashMap<>();

    LocalDate localDate = LocalDate.now();

    public StatusService() {
        report_service = ApplicationProperties.getInstance().get("app.reportservice", "");
        report_service = report_service.replaceFirst("/$", "");
        if (report_service.isEmpty()) {
            throw new RuntimeException("app.reportservice must be present in the app config");
        }
        uib_health_service = ApplicationProperties.getInstance().get("app.uib-health", "");
        uib_health_service = uib_health_service.replaceFirst("/$", "");
        if (uib_health_service.isEmpty()) {
            throw new RuntimeException("app.uib-health must be present in the app config");
        }

        // Just to get some data for UI work...
        // TODO - read existing map from db/file
        initializebackDateswithStructure();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        try {

                            getUserSessionStatusForToday();


                        } catch (Exception ex) {
                            logger.error("Exception in trying to get updated status", ex);
                            ex.printStackTrace();
                        }
                    }
                },
                1, 1, TimeUnit.MINUTES);
    }


    public UserSessionStatus getUserSessionStatusForToday() {

        try {
            if (lastUpdatedStatusCache.getStarttime_of_today() == null || lastUpdatedStatusCache.getStarttime_of_today().plusDays(1).isBefore(ZonedDateTime.now())) {
                //reset the cache if the day is passed
                // TODO: store last days stats in DB
                recentStatus = null;
                lastUpdatedStatusCache = new UserSessionStatusCache();
                ZonedDateTime starttime_of_today = ZonedDateTime.now().with(LocalTime.MIDNIGHT);
                lastUpdatedStatusCache.setStarttime_of_today(starttime_of_today);
            }

            String starttime_param = String.valueOf(lastUpdatedStatusCache.getStarttime_of_today().toInstant().toEpochMilli());
            if (lastUpdatedStatusCache.getLasttime_requested() != 0) {
                starttime_param = String.valueOf(lastUpdatedStatusCache.getLasttime_requested() + 1);
            }
            ZonedDateTime lastTimeRequested = ZonedDateTime.now();
            String endtime_pram = String.valueOf(lastTimeRequested.toInstant().toEpochMilli());

            ActivityStatistics stats = Unirest.get(report_service + "/observe/statistics/all/usersession")
                    .queryString("starttime", starttime_param)
                    .queryString("endtime", endtime_pram)
                    .asObject(ActivityStatistics.class)
                    .getBody();

            UserSessionStatus status = getDataFromActivityStatistics(stats);
            status.setTotal_number_of_users(getTotalOfUsers());
            recentStatus = status;
            String todayString = simpleDateFormatter.format(new Date());
            DailyStatus dailyStatus = dailyStatusMap.get(todayString);
            if (dailyStatus == null) {
                dailyStatus = new DailyStatus();
                //dailyStatusMap.put(new SimpleDateFormat("yyyy-MM-dd").format(new Date()),dailyStatus);
            }
            if (dailyStatus.getUserApplicationStatistics() == null) {
                dailyStatus.setUserApplicationStatistics(new UserApplicationStatistics());
            }
            dailyStatus.setUserSessionStatus(status);
            dailyStatusMap.put(todayString, dailyStatus);
            return status;
        } catch (Exception ex) {
            logger.error("get: %s", ex.getMessage());
            throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500.addMessageParams("Failed to get status due to the exception - " + ex.getMessage());
        }

    }


    protected int getTotalOfUsers() {
        try {
            String uib_health_service = ApplicationProperties.getInstance().get("app.uib-health", "");
            uib_health_service = uib_health_service.replaceFirst("/$", "");
            if (uib_health_service.isEmpty()) {
                throw new RuntimeException("app.uib-health must be present in the app config");
            }

            String count_from_uib = Unirest.get(uib_health_service)
                    .asJson()
                    .getBody()
                    .getObject()
                    .getString("users (DB)");
            return Integer.valueOf(count_from_uib != null ? count_from_uib : "0");
        } catch (UnirestException e) {
            logger.error("get: %s", e.getMessage());
        }
        return 0;
    }

    protected UserSessionStatus getDataFromActivityStatistics(ActivityStatistics stats) {
        ActivityCollection activities = stats.getActivities();
        UserSessionStatus status = new UserSessionStatus();
        Set<String> logins = new HashSet<>(lastUpdatedStatusCache.getLogins());
        Set<String> registered_users = new HashSet<>(lastUpdatedStatusCache.getRegistered_users());
        Set<String> deleted_users = new HashSet<>(lastUpdatedStatusCache.getDeleted_users());


        activities.getUserSessions().stream().filter(i -> i.getData().getApplicationid().equals("2215")).forEach(activity -> {
            if (activity.getData().getUsersessionfunction().equalsIgnoreCase("userSessionAccess")) {
                logins.add(activity.getData().getUsersessionfunction() + "" + activity.getData().getUserid());
            } else if (activity.getData().getUsersessionfunction().equalsIgnoreCase("userCreated")) {
                registered_users.add(activity.getData().getUsersessionfunction() + "" + activity.getData().getUserid());
            } else if (activity.getData().getUsersessionfunction().equalsIgnoreCase("userDeleted")) {
                deleted_users.add(activity.getData().getUsersessionfunction() + "" + activity.getData().getUserid());
            }
        });
        status.setNumber_of_deleted_users_today(deleted_users.size());
        status.setNumber_of_logins_today(logins.size());
        status.setNumber_of_registered_users_today(registered_users.size());
        status.setLast_updated(ZonedDateTime.now());
        status.setStarttime_of_today(lastUpdatedStatusCache.getStarttime_of_today());
        //store to the cache
        lastUpdatedStatusCache.setLogins(logins);
        lastUpdatedStatusCache.setRegistered_users(registered_users);
        lastUpdatedStatusCache.setDeleted_users(deleted_users);
        lastUpdatedStatusCache.setLasttime_requested(stats.getEndTime());


        return status;
    }


    public UserSessionStatus getRecentStatus() {
        if (recentStatus != null) {
            return recentStatus;
        } else {
            return getUserSessionStatusForToday();
        }
    }

    public Map<String, DailyStatus> getRecentStatusMap() {
        if (recentStatus == null) {
            getUserSessionStatusForToday();
        }
        return dailyStatusMap;
    }


    private void initializebackDateswithStructure() {
        LocalDate yesterdayDate = localDate.minusDays(1);
        String yesterdayString = yesterdayDate.format(datetimeformatter);
        DailyStatus dailyStatus = new DailyStatus();
        dailyStatus.setUserApplicationStatistics(new UserApplicationStatistics());
        dailyStatus.setUserSessionStatus(new UserSessionStatus());
        dailyStatusMap.put(yesterdayString, dailyStatus);
        LocalDate yesteryesterdayDate = localDate.minusDays(2);
        String yesteryesterdayString = yesteryesterdayDate.format(datetimeformatter);
        dailyStatus = new DailyStatus();
        dailyStatus.setUserApplicationStatistics(new UserApplicationStatistics());
        dailyStatus.setUserSessionStatus(new UserSessionStatus());
        dailyStatusMap.put(yesteryesterdayString, dailyStatus);
        LocalDate yesteryesteryesterdayDate = localDate.minusDays(3);
        String yesteryesteryesterdayString = yesteryesteryesterdayDate.format(datetimeformatter);
        dailyStatus = new DailyStatus();
        dailyStatus.setUserApplicationStatistics(new UserApplicationStatistics());
        dailyStatus.setUserSessionStatus(new UserSessionStatus());
        dailyStatusMap.put(yesteryesteryesterdayString, dailyStatus);
    }


}
