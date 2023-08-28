package no.cantara.tools.stats.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

public class UserApplicationStatistics {

    private String for_application;
    private int number_of_registered_users_today;
    private int number_of_logins_today;
    private int number_of_deleted_users_today;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime last_updated;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime starttime_of_today;

}
