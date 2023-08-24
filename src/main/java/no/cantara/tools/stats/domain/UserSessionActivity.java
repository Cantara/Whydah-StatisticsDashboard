package no.cantara.tools.stats.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionActivity {

	String prefix="";
	String name="usersession";
	long startTime=0;
	UserSessionActivityData data;
    
}
