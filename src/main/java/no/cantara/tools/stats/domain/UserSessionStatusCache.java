package no.cantara.tools.stats.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSessionStatusCache  implements Serializable {
	Set<String> logins = new HashSet<>();
	Set<String> registered_users = new HashSet<>();
	Set<String> deleted_users = new HashSet<>();
	ZonedDateTime starttime_of_today = null;
	long lasttime_requested = 0;

}
