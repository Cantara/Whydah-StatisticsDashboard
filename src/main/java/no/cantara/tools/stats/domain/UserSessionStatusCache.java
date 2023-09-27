package no.cantara.tools.stats.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.*;

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
	Map<String, HashSet<String>> logins_by_appId = new HashMap<String, HashSet<String>>(); 
	Map<String, HashSet<String>> registered_users_by_appId = new HashMap<String, HashSet<String>>();
	Map<String, HashSet<String>> deleted_users_by_appId = new HashMap<String, HashSet<String>>();
	ZonedDateTime starttime_of_today = null;
	long lasttime_requested = 0;

	public int getAllRegisteredUsers(){
		Collection<HashSet<String>> values = registered_users_by_appId.values();
		try {
			return values.stream().iterator().next().size() + registered_users.size();
		} catch (Exception e){
			return  registered_users.size();
		}
	}

	public int getAllRegisteredLogins(){
		Collection<HashSet<String>> values = logins_by_appId.values();
		try {
			return values.stream().iterator().next().size() + logins.size();
		} catch (Exception e){
			return  logins.size();
		}
	}

	public int getAllRegisteredDeletions(){
		Collection<HashSet<String>> values = deleted_users_by_appId.values();
		try {
			return values.stream().iterator().next().size()+deleted_users.size();
		} catch (Exception e){
			return  deleted_users.size();
		}
	}
}
