package no.cantara.tools.stats.status;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.cantara.tools.stats.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import no.cantara.config.ApplicationProperties;
import no.cantara.tools.stats.exception.AppExceptionCode;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StatusService {

    public static final Logger logger = LoggerFactory.getLogger(StatusService.class);

    private static final String MAPFILENAME = "data/dailyStatusMap.json";
    ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature())
            .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature())
            .enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature())
            .findAndRegisterModules();
    String report_service;
    String uib_health_service;
    String sts_health_service;

    public static final String dateformat = "yyyy-MM-dd";

    public static final String hourformat = "yyyy-MM-dd:HH";

    private static boolean bootstrap = true;

    public static final SimpleDateFormat simpleDateFormatter = new SimpleDateFormat(dateformat);
    public static final SimpleDateFormat simpleHourFormatter = new SimpleDateFormat(hourformat);

    private static UserSessionStatusCache lastUpdatedStatusCache = new UserSessionStatusCache();

    private static TreeMap<String, UserSessionStatusCache> lastHourUpdatedStatusCache = new TreeMap<>();
    private static UserSessionStatus recentStatus = null;
    private static TreeMap<String, DailyStatus> dailyStatusMap = new TreeMap<>();

    private static TreeMap<String, HourlyStatus> hourlyStatusMap = new TreeMap<>();

    private static String currentHour;


    public StatusService() {
        String timezone = ApplicationProperties.getInstance().get("app.stats.timezone", "Europe/Oslo");
        simpleDateFormatter.setTimeZone(TimeZone.getTimeZone(timezone));
        simpleHourFormatter.setTimeZone(TimeZone.getTimeZone(timezone));
        report_service = ApplicationProperties.getInstance().get("app.reportservice", "");
        report_service = report_service.replaceFirst("/$", "");
        if (report_service.isEmpty()) {
            logger.warn("Unable to get report_service");
            throw new RuntimeException("app.reportservice must be present in the app config");
        }
        uib_health_service = ApplicationProperties.getInstance().get("app.uib-health", "");
        uib_health_service = uib_health_service.replaceFirst("/$", "");
        if (uib_health_service.isEmpty()) {
            logger.warn("Unable to get uib_health_service");
            throw new RuntimeException("app.uib-health must be present in the app config");
        }
        sts_health_service = ApplicationProperties.getInstance().get("app.sts-health", "");
        sts_health_service = sts_health_service.replaceFirst("/$", "");
        if (sts_health_service.isEmpty()) {
            logger.warn("Unable to get sts_health_service");
            throw new RuntimeException("app.sts-health must be present in the app config");
        }

        try {
            Files.createDirectories(Paths.get("data"));
        } catch (Exception e) {
            logger.error("Unable to create path to persistence", e);
        }
        try {
            currentHour = simpleHourFormatter.format(new Date());
            if (readMap() != null) {
                dailyStatusMap = readMap();
                String todayString = simpleDateFormatter.format(new Date());
                if (dailyStatusMap.get(todayString) != null && dailyStatusMap.get(todayString).getUserSessionStatus() != null) {
                    recentStatus = dailyStatusMap.get(todayString).getUserSessionStatus();
                    recentStatus.setStarttime_of_this_day(ZonedDateTime.now());
                    // Workaround for handling serialized null
                    if (dailyStatusMap.get(todayString).getUserApplicationStatistics() == null) {
                        UserApplicationStatistics userApplicationStatistics = new UserApplicationStatistics();
                        userApplicationStatistics.setFor_application("2015");
                        userApplicationStatistics.setLast_updated(ZonedDateTime.now());
                        List<UserApplicationStatistics> userApplicationStatisticsList = new ArrayList<>();
                        userApplicationStatisticsList.add(userApplicationStatistics);
                        dailyStatusMap.get(todayString).setUserApplicationStatistics(userApplicationStatisticsList);
                    }
                }

            }
        } catch (Exception e) {
            logger.error("Unable to create status domain models", e);
        }

        try {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(
                    new Runnable() {
                        public void run() {
                            try {

                                getUserSessionStatusForToday();

                                //remove old data
                                int maxHistory = ApplicationProperties.getInstance().asInt("app.stats.keep-history-in-days", 7);
                                int numberToRemove = dailyStatusMap.size() - maxHistory - 1;
                                while (numberToRemove > 0) {
                                    Entry<String, DailyStatus> en = dailyStatusMap.pollFirstEntry();
                                    numberToRemove--;
                                    logger.debug("remove date {}. We keep a history of {} statistics records according to the config", en.getKey(), maxHistory);
                                }

                                //store map
                                storeMap();

                            } catch (Exception ex) {
                                logger.error("Exception in trying to get updated status", ex);
                                ex.printStackTrace();
                            }
                        }
                    },
                    1, 1, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.error("Unable to create status pollers", e);
        }

    }


    public UserSessionStatus getUserSessionStatusForToday() {

        UserSessionStatus status = recentStatus;
        if (status == null) {
            status = new UserSessionStatus();
            status.setLast_updated(ZonedDateTime.now());

        }
        currentHour = simpleHourFormatter.format(new Date());
        try {
            if (lastUpdatedStatusCache.getStarttime_of_today() == null || lastUpdatedStatusCache.getStarttime_of_today().plusDays(1).isBefore(ZonedDateTime.now())) {
                //reset the cache if the day is passed
                // TODO: store last days stats in DB
                recentStatus = null;
                lastUpdatedStatusCache = new UserSessionStatusCache();
                ZonedDateTime starttime_of_today = ZonedDateTime.now().with(LocalTime.MIDNIGHT);
                lastUpdatedStatusCache.setStarttime_of_today(starttime_of_today);


                // add a new dailystatus object for the new day
                DailyStatus dailyStatus = new DailyStatus();
                recentStatus = new UserSessionStatus();
                recentStatus.setStarttime_of_this_day(starttime_of_today);
                recentStatus.setLast_updated(ZonedDateTime.now());

                dailyStatus.setUserSessionStatus(recentStatus);
                String todayString = simpleDateFormatter.format(new Date());
                if (dailyStatusMap.get(todayString) == null) {

                    dailyStatusMap.put(todayString, dailyStatus);
                }
            }
//            if (lastHourUpdatedStatusCache.get(currentHour)==null || lastHourUpdatedStatusCache.get(currentHour).getStarttime_of_today() == null || lastHourUpdatedStatusCache.get(currentHour).getStarttime_of_today().plusHours(1).isBefore(ZonedDateTime.now())) {
//                lastHourUpdatedStatusCache.put(currentHour, new UserSessionStatusCache());

//                HourlyStatus hourlyStatus = updateHourlyStatus();
//                hourlyStatusMap.put(currentHour, hourlyStatus);

//            }

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

            for (UserSessionActivity activity : stats.getActivities().getUserSessions()) {
                if (activity.getStartTime() == 0) {
                    activity.setStartTime(System.currentTimeMillis());
                }
            }
            status = getUserSessionStatusDataFromActivityStatistics(stats);
            status.setTotal_number_of_users(getTotalOfUsers());
            status.setNumber_of_active_user_sessions(getTotalOfSessions());
            status.setTotal_number_of_applications(getTotalOfApplications());
            recentStatus = status;
            String todayString = simpleDateFormatter.format(new Date());
            DailyStatus dailyStatus = dailyStatusMap.get(todayString);
            if (dailyStatus == null) {
                dailyStatus = new DailyStatus();

                //dailyStatusMap.put(new SimpleDateFormat("yyyy-MM-dd").format(new Date()),dailyStatus);
            }
            if (dailyStatus.getUserApplicationStatistics() == null) {
                String appIds = ApplicationProperties.getInstance().get("app.stats.appids", "");
                if (!appIds.equals("")) {
                    String[] parts = appIds.split("\\s*[;,]\\s*");
                    dailyStatus.setUserApplicationStatistics(getUserApplicationStatisticsDataFromActivityStatistics(new HashSet<String>(Arrays.asList(parts)), stats));
                }
            } else {
                String appIds = ApplicationProperties.getInstance().get("app.stats.appids", "");
                if (!appIds.equals("")) {
                    String[] parts = appIds.split("\\s*[;,]\\s*");
                    dailyStatus.setUserApplicationStatistics(getUserApplicationStatisticsDataFromActivityStatistics(new HashSet<String>(Arrays.asList(parts)), stats));
                }
            }
            HourlyStatus hourlyStatus = updateHourlyStatus();
            dailyStatus.setUserSessionStatus(status);
            dailyStatus.setActivityStatistics(stats);
            dailyStatus.addActivityStatistics(stats.getActivities().getUserSessions());
            dailyStatus.setHourlyStatus(currentHour, hourlyStatus);
            dailyStatusMap.put(todayString, dailyStatus);
            hourlyStatus = updateHourlyStatus();
            hourlyStatusMap.put(currentHour, hourlyStatus);

            return status;
        } catch (Exception ex) {
            logger.error("get: %s", ex.getMessage());
            return status;
            //throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500.addMessageParams("Failed to get status due to the exception - " + ex.getMessage());
        }

    }

    private HourlyStatus updateHourlyStatus() {
        HourlyStatus hourlyStatus = hourlyStatusMap.get(currentHour);
        if (hourlyStatus == null) {
            hourlyStatus = new HourlyStatus();

        }
        int logins = lastHourUpdatedStatusCache.get(currentHour).getAllRegisteredLogins();
        int registered_users = lastHourUpdatedStatusCache.get(currentHour).getAllRegisteredUsers();
        int deleted_users = lastHourUpdatedStatusCache.get(currentHour).getAllRegisteredDeletions();
        // survive restart scenario
        if (bootstrap == true) {
            if (hourlyStatus.getNumber_of_unique_logins_this_hour() > logins) {
                hourlyStatus.setNumber_of_unique_logins_this_hour(hourlyStatus.getNumber_of_unique_logins_this_hour() + logins);

            } else {
                hourlyStatus.setNumber_of_unique_logins_this_hour(logins);
            }
            if (hourlyStatus.getNumber_of_registered_users_this_hour() > registered_users) {
                hourlyStatus.setNumber_of_registered_users_this_hour(hourlyStatus.getNumber_of_registered_users_this_hour() + registered_users);
            } else {
                hourlyStatus.setNumber_of_registered_users_this_hour(registered_users);
            }
            if (hourlyStatus.getNumber_of_deleted_users_this_day() > logins) {
                hourlyStatus.setNumber_of_deleted_users_this_day(hourlyStatus.getNumber_of_deleted_users_this_day() + deleted_users);
            } else {
                hourlyStatus.setNumber_of_deleted_users_this_day(deleted_users);
            }
            bootstrap = false;
        } else {
            hourlyStatus.setNumber_of_unique_logins_this_hour(logins);
            hourlyStatus.setNumber_of_registered_users_this_hour(registered_users);
            hourlyStatus.setNumber_of_deleted_users_this_day(deleted_users);

        }
        return hourlyStatus;
    }

    protected int getTotalOfUsers() {
        try {

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

    protected int getTotalOfSessions() {
        try {

            String count_from_sts = Unirest.get(sts_health_service)
                    .asJson()
                    .getBody()
                    .getObject()
                    .getString("AuthenticatedUserTokenMapSize");
            return Integer.valueOf(count_from_sts != null ? count_from_sts : "0");
        } catch (UnirestException e) {
            logger.error("get: %s", e.getMessage());
        }
        return 0;
    }

    protected int getTotalOfApplications() {
        try {

            String count_from_sts = Unirest.get(sts_health_service)
                    .asJson()
                    .getBody()
                    .getObject()
                    .getString("ConfiguredApplications");
            return Integer.valueOf(count_from_sts != null ? count_from_sts : "0");
        } catch (UnirestException e) {
            logger.error("get: %s", e.getMessage());
        }
        return 0;
    }

    protected int getTotalOfThreats() {
        try {


            String count_from_sts = Unirest.get(sts_health_service)
                    .asJson()
                    .getBody()
                    .getObject()
                    .getString("ThreatSignalRingbufferSize");
            return Integer.valueOf(count_from_sts != null ? count_from_sts : "0");
        } catch (UnirestException e) {
            logger.error("get: %s", e.getMessage());
        }
        return 0;
    }

    protected List<UserApplicationStatistics> getUserApplicationStatisticsDataFromActivityStatistics(Set<String> appIds, ActivityStatistics stats) {
        Map<String, UserApplicationStatistics> statsByAppId = new HashMap<String, UserApplicationStatistics>();
        appIds.forEach(id -> statsByAppId.put(id, new UserApplicationStatistics(id)));
        if (lastHourUpdatedStatusCache.get(currentHour) == null) {
            lastHourUpdatedStatusCache.put(currentHour, new UserSessionStatusCache());
        }
        if (stats != null) {
            ActivityCollection activities = stats.getActivities();
            String todayString = simpleDateFormatter.format(new Date());
            activities.getUserSessions().stream().filter(i -> i.getData().getApplicationid() != null).forEach(activity -> {
                if (appIds.contains(activity.getData().getApplicationid()) && todayString.equalsIgnoreCase(simpleDateFormatter.format(Date.from(Instant.ofEpochMilli(activity.getStartTime()))))) {

                    if (activity.getData().getUsersessionfunction().equalsIgnoreCase("userSessionAccess")) {
                        if (!lastUpdatedStatusCache.getLogins_by_appId().containsKey(activity.getData().getApplicationid())) {
                            lastUpdatedStatusCache.getLogins_by_appId().put(activity.getData().getApplicationid(), new HashSet<>());
                        }
                        lastUpdatedStatusCache.getLogins_by_appId().get(activity.getData().getApplicationid()).add(activity.getData().getUsersessionfunction() + "" + activity.getData().getUserid());
                    } else if (activity.getData().getUsersessionfunction().equalsIgnoreCase("userCreated")) {
                        if (!lastUpdatedStatusCache.getRegistered_users_by_appId().containsKey(activity.getData().getApplicationid())) {
                            lastUpdatedStatusCache.getRegistered_users_by_appId().put(activity.getData().getApplicationid(), new HashSet<>());
                        }
                        lastUpdatedStatusCache.getRegistered_users_by_appId().get(activity.getData().getApplicationid()).add(activity.getData().getUsersessionfunction() + "" + activity.getData().getUserid());
                    } else if (activity.getData().getUsersessionfunction().equalsIgnoreCase("userDeleted")) {
                        if (!lastUpdatedStatusCache.getDeleted_users_by_appId().containsKey(activity.getData().getApplicationid())) {
                            lastUpdatedStatusCache.getDeleted_users_by_appId().put(activity.getData().getApplicationid(), new HashSet<>());
                        }
                        lastUpdatedStatusCache.getDeleted_users_by_appId().get(activity.getData().getApplicationid()).add(activity.getData().getUsersessionfunction() + "" + activity.getData().getUserid());
                    }
                    if (appIds.contains(activity.getData().getApplicationid()) && currentHour.equalsIgnoreCase(simpleHourFormatter.format(Date.from(Instant.ofEpochMilli(activity.getStartTime()))))) {
                        if (lastHourUpdatedStatusCache.get(currentHour).getLogins_by_appId() == null) {
                            lastHourUpdatedStatusCache.put(currentHour, new UserSessionStatusCache());
                        }
                        if (activity.getData().getUsersessionfunction().equalsIgnoreCase("userSessionAccess")) {
                            if (!lastHourUpdatedStatusCache.get(currentHour).getLogins_by_appId().containsKey(activity.getData().getApplicationid())) {
                                lastHourUpdatedStatusCache.get(currentHour).getLogins_by_appId().put(activity.getData().getApplicationid(), new HashSet<>());
                            }
                            lastHourUpdatedStatusCache.get(currentHour).getLogins_by_appId().get(activity.getData().getApplicationid()).add(activity.getData().getUsersessionfunction() + "" + activity.getData().getUserid());
                        } else if (activity.getData().getUsersessionfunction().equalsIgnoreCase("userCreated")) {
                            if (!lastHourUpdatedStatusCache.get(currentHour).getRegistered_users_by_appId().containsKey(activity.getData().getApplicationid())) {
                                lastHourUpdatedStatusCache.get(currentHour).getRegistered_users_by_appId().put(activity.getData().getApplicationid(), new HashSet<>());
                            }
                            lastHourUpdatedStatusCache.get(currentHour).getRegistered_users_by_appId().get(activity.getData().getApplicationid()).add(activity.getData().getUsersessionfunction() + "" + activity.getData().getUserid());
                        } else if (activity.getData().getUsersessionfunction().equalsIgnoreCase("userDeleted")) {
                            if (!lastHourUpdatedStatusCache.get(currentHour).getDeleted_users_by_appId().containsKey(activity.getData().getApplicationid())) {
                                lastHourUpdatedStatusCache.get(currentHour).getDeleted_users_by_appId().put(activity.getData().getApplicationid(), new HashSet<>());
                            }
                            lastHourUpdatedStatusCache.get(currentHour).getDeleted_users_by_appId().get(activity.getData().getApplicationid()).add(activity.getData().getUsersessionfunction() + "" + activity.getData().getUserid());
                        }
                    }
                }
            });
            appIds.forEach(id -> {
                UserApplicationStatistics d = statsByAppId.get(id);
                d.setFor_application(id);
                d.setLast_updated(ZonedDateTime.now());
                d.setNumber_of_deleted_users_this_day(lastUpdatedStatusCache.getDeleted_users_by_appId().get(id) != null ? lastUpdatedStatusCache.getDeleted_users_by_appId().get(id).size() : 0);
                d.setNumber_of_registered_users_this_day(lastUpdatedStatusCache.getRegistered_users_by_appId().get(id) != null ? lastUpdatedStatusCache.getRegistered_users_by_appId().get(id).size() : 0);
                d.setNumber_of_unique_logins_this_day(lastUpdatedStatusCache.getLogins_by_appId().get(id) != null ? lastUpdatedStatusCache.getLogins_by_appId().get(id).size() : 0);
                d.setStarttime_of_this_day(lastUpdatedStatusCache.getStarttime_of_today());
                statsByAppId.put(id, d);
            });
        }
        return new ArrayList<UserApplicationStatistics>(statsByAppId.values());

    }

    protected UserSessionStatus getUserSessionStatusDataFromActivityStatistics(ActivityStatistics stats) {
        UserSessionStatus status = new UserSessionStatus();
        status.setLast_updated(ZonedDateTime.now());
        if (stats != null) {

            ActivityCollection activities = stats.getActivities();
            Set<String> logins = new HashSet<>(lastUpdatedStatusCache.getLogins());
            Set<String> registered_users = new HashSet<>(lastUpdatedStatusCache.getRegistered_users());
            Set<String> deleted_users = new HashSet<>(lastUpdatedStatusCache.getDeleted_users());
            String todayString = simpleDateFormatter.format(new Date());

            activities.getUserSessions().stream().filter(i -> i.getData().getApplicationid() != null).forEach(activity -> {
                if (todayString.equalsIgnoreCase(simpleDateFormatter.format(Date.from(Instant.ofEpochMilli(activity.getStartTime()))))) {
                    if (activity.getData().getUsersessionfunction().equalsIgnoreCase("userSessionAccess")) {
                        logins.add(activity.getData().getUsersessionfunction() + "" + activity.getData().getUserid());
                        // increase session activity count  - may count twice...
                        status.setTotal_number_of_session_actions_this_day(1 + status.getTotal_number_of_session_actions_this_day());
                    } else if (activity.getData().getUsersessionfunction().equalsIgnoreCase("userCreated")) {
                        registered_users.add(activity.getData().getUsersessionfunction() + "" + activity.getData().getUserid());
                    } else if (activity.getData().getUsersessionfunction().equalsIgnoreCase("userDeleted")) {
                        deleted_users.add(activity.getData().getUsersessionfunction() + "" + activity.getData().getUserid());
                    } else if (activity.getData().getUsersessionfunction().equalsIgnoreCase("userSessionVerification")) {
                        // increase session activity count  - may count twice...
                        status.setTotal_number_of_session_actions_this_day(1 + status.getTotal_number_of_session_actions_this_day());
                    }
                }
            });
            status.setNumber_of_deleted_users_this_day(deleted_users.size());
            status.setNumber_of_unique_logins_this_day(logins.size());
            status.setNumber_of_registered_users_this_day(registered_users.size());
            status.setLast_updated(ZonedDateTime.now());
            status.setStarttime_of_this_day(lastUpdatedStatusCache.getStarttime_of_today());
            //store to the cache
            lastUpdatedStatusCache.setLogins(logins);
            lastUpdatedStatusCache.setRegistered_users(registered_users);
            lastUpdatedStatusCache.setDeleted_users(deleted_users);
            lastUpdatedStatusCache.setLasttime_requested(stats.getEndTime());
        }

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

    public synchronized void storeMap() {
        try {

            Path path = Paths.get(MAPFILENAME);
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dailyStatusMap);
            Files.writeString(path, json, StandardCharsets.UTF_8);

        } catch (FileNotFoundException e) {
            logger.error("File not found", e);
        } catch (IOException e) {
            logger.error("Error initializing stream", e);
        }
    }

    public TreeMap<String, DailyStatus> readMap() {
        try {
            TypeReference<TreeMap<String, DailyStatus>> typeRef = new TypeReference<TreeMap<String, DailyStatus>>() {
            };
            TreeMap<String, DailyStatus> dailyStatusMap = mapper.readValue(new File(MAPFILENAME), typeRef);
            return dailyStatusMap;
        } catch (FileNotFoundException e) {
            logger.error("File not found", e);
        } catch (IOException e) {
            logger.error("Error initializing stream", e);
        }
        //fallback
        try {
            TypeReference<HashMap<String, DailyStatus>> typeRef = new TypeReference<HashMap<String, DailyStatus>>() {
            };
            Map<String, DailyStatus> dailyStatusMap = mapper.readValue(new File(MAPFILENAME), typeRef);
            return new TreeMap<>(dailyStatusMap);
        } catch (FileNotFoundException e) {
            logger.error("File not found", e);
        } catch (IOException e) {
            logger.error("Error initializing stream", e);
        }
        return null;

    }
}

