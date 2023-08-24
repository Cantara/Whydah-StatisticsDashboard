package no.cantara.tools.stats.domain;


import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UserSessionStatus {
	
	private int total_number_of_users;
	private int number_of_registered_users;
	private int number_of_logins;
	private int number_of_deleted_users;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime last_updated;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime starttime_of_today;
	

}
