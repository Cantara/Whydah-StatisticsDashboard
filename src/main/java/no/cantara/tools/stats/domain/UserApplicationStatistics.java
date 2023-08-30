package no.cantara.tools.stats.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class UserApplicationStatistics implements Serializable {

    private String for_application;
    private int number_of_registered_users_this_day;
    private int number_of_unique_logins_this_day;
    private int number_of_deleted_users_this_day;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime last_updated;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime starttime_of_this_day;

}
