package no.cantara.tools.stats.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionActivity  implements Serializable {

	String prefix="";
	String name="usersession";
	long startTime=0;
	UserSessionActivityData data=new UserSessionActivityData();
    
}
