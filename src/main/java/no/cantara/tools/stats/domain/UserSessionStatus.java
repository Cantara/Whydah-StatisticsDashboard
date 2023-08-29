package no.cantara.tools.stats.domain;


import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UserSessionStatus {
	
	private int total_number_of_users;
	private int number_of_registered_users_today;
	private int number_of_unique_logins_today;
	private int total_number_of_session_actions_today;
	private int number_of_active_user_sessions;

	private int number_of_deleted_users_today;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime last_updated;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime starttime_of_today;
	

}
