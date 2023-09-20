package no.cantara.tools.stats.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class HourlyStatus implements Serializable {

    private int number_of_registered_users_this_hour;
    private int number_of_unique_logins_this_hour;
    private int number_of_deleted_users_this_day;
}
