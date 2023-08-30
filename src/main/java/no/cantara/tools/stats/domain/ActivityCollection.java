package no.cantara.tools.stats.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCollection implements Serializable {

	List<UserSessionActivity> userSessions = new ArrayList<>();
	
}
