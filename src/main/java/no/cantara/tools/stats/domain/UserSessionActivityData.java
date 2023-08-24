package no.cantara.tools.stats.domain;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionActivityData {

	private String usersessionfunction;
	private String applicationid;
	private String userid;
	private String applicationtokenid;
}
