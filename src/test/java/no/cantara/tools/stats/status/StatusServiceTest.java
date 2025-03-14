package no.cantara.tools.stats.status;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import no.cantara.config.ApplicationProperties;
import no.cantara.config.testsupport.ApplicationPropertiesTestHelper;
import no.cantara.tools.stats.domain.ActivityStatistics;
import no.cantara.tools.stats.domain.UserSessionStatus;
import no.cantara.tools.stats.utils.EntityUtils;

import java.time.Instant;

class StatusServiceTest {

    static {
        ApplicationPropertiesTestHelper.enableMutableSingleton();
        ApplicationProperties.builder().testDefaults().buildAndSetStaticSingleton();
    }
    
    @Test
    public void testParseResponseToUserSessionStatus() throws JsonMappingException, JsonProcessingException {
		Long timestamp = Instant.now().toEpochMilli();
    	String responseFromReportService = "{\n"
    			+ "    \"prefix\":\"All\",\n"
    			+ "    \"activityName\":\"userSession\",\n"
    			+ "    \"startTime\":"+timestamp+",\n"
    			+ "    \"endTime\":" + timestamp +",\n"
    			+ "    \"activities\":{\n"
    			+ "        \"userSessions\":[\n"
    			+ "            {\n"
    			+ "                \"prefix\":\"\",\n"
    			+ "                \"name\":\"usersession\",\n"
    			+ "                \"startTime\":" + timestamp +",\n"
    			+ "                \"data\":{\n"
    			+ "                    \"usersessionfunction\":\"userSessionVerification\",\n"
    			+ "                    \"applicationid\":\"2215\",\n"
    			+ "                    \"userid\":\"petter\",\n"
    			+ "                    \"applicationtokenid\":\"f4d6c85374511282d8f515855c489992\"\n"
    			+ "                }\n"
    			+ "            },\n"
    			+ "            {\n"
    			+ "                \"prefix\":\"\",\n"
    			+ "                \"name\":\"usersession\",\n"
    			+ "                \"startTime\":" + timestamp +",\n"
    			+ "                \"data\":{\n"
    			+ "                    \"usersessionfunction\":\"userSessionAccess\",\n"
    			+ "                    \"applicationid\":\"2215\",\n"
    			+ "                    \"userid\":\"petter\",\n"
    			+ "                    \"applicationtokenid\":\"f4d6c85374511282d8f515855c489992\"\n"
    			+ "                }\n"
    			+ "            },\n"
    			+ "            {\n"
    			+ "                \"prefix\":\"\",\n"
    			+ "                \"name\":\"usersession\",\n"
    			+ "                \"startTime\":" + timestamp +",\n"
    			+ "                \"data\":{\n"
    			+ "                    \"usersessionfunction\":\"userCreated\",\n"
    			+ "                    \"applicationid\":\"2215\",\n"
    			+ "                    \"userid\":\"97038419\",\n"
    			+ "                    \"applicationtokenid\":\"2891fe8f9a6a81a96d30b41b879b6009\"\n"
    			+ "                }\n"
    			+ "            },\n"
    			+ "            {\n"
    			+ "                \"prefix\":\"\",\n"
    			+ "                \"name\":\"usersession\",\n"
    			+ "                \"startTime\":" + timestamp +",\n"
    			+ "                \"data\":{\n"
    			+ "                    \"usersessionfunction\":\"userDeleted\",\n"
    			+ "                    \"applicationid\":\"2215\",\n"
    			+ "                    \"userid\":\"97038411\",\n"
    			+ "                    \"applicationtokenid\":\"2891fe8f9a6a81a96d30b41b879b6009\"\n"
    			+ "                }\n"
    			+ "            }\n"
    			+ "        ]\n"
    			+ "    }\n"
    			+ "}";
    	ActivityStatistics map_from_the_response = EntityUtils.mapFromJson(responseFromReportService, ActivityStatistics.class);
    	StatusService s = new StatusService();
    	UserSessionStatus status = s.getUserSessionStatusDataFromActivityStatistics(map_from_the_response);
    	status.setTotal_number_of_users(s.getTotalOfUsers());
    	assertTrue(status!=null);
    	assertTrue(status.getNumber_of_deleted_users_this_day()==1);
    	assertTrue(status.getNumber_of_unique_logins_this_day() == 1);
    	assertTrue(status.getNumber_of_registered_users_this_day() ==1);
    	assertTrue(status.getTotal_number_of_users()>0);
    	
    }

    
}