package no.cantara.tools.stats.status;

import java.net.URI;
import java.net.URL;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import no.cantara.tools.stats.domain.Environment;
import no.cantara.tools.stats.utils.EnvironmentConfig;
import javax.ws.rs.core.UriBuilder;

import no.cantara.tools.stats.domain.DailyStatus;
import no.cantara.tools.stats.utils.EnvironmentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import io.netty.handler.codec.http.QueryStringDecoder;
import no.cantara.tools.stats.domain.UserSessionStatus;
import no.cantara.tools.stats.exception.AppExceptionCode;
import no.cantara.tools.stats.domain.UserSessionStatus;

public class StatusResource implements Service {

    private static final Logger logger = LoggerFactory.getLogger(StatusResource.class);

    private static final String ACCESS_TOKEN_PARAM_NAME = "accessToken";

    final StatusService statusService = new StatusService();

    private final EnvironmentConfig environmentConfig = new EnvironmentConfig();

    final String accessToken;

    public StatusResource(String accessToken) {
        this.accessToken = accessToken;
        
    }

    /**
     * A service registers itself by updating the routine rules.
     *
     * @param rules the routing rules.
     */
    @Override
    public void update(Routing.Rules rules) {
        rules
                .get("/status", this::showUserSessionStatus)
                .get("/api/status", this::showUserSessionStatus)
                .get("/env", this::showEnvironment)
                .get("/api/env", this::showEnvironment)
                .options("/status", this::showUserSessionStatusOptionHeaders)
                .options("/api/status", this::showUserSessionStatusOptionHeaders);
    }
    
    private String getAccessTokenInReferer(URI uri) {
    	Map<String, List<String>> parameters =  new QueryStringDecoder(uri).parameters();
    	if( parameters.get("accesstoken")!=null) { 
    		return parameters.get("accesstoken").get(0);
    	} else if(parameters.get("accessToken")!=null) {
    		return parameters.get("accessToken").get(0);
    	}
    	return null;
    }
    

    
    @SuppressWarnings("checkstyle:designforextension")
    public void showUserSessionStatus(final ServerRequest request, final ServerResponse response){
        if (accessToken != null && accessToken.length() > 0) {
            String AccessTokenParam = request.queryParams().first(ACCESS_TOKEN_PARAM_NAME).
            		orElseGet(() -> getAccessTokenInReferer(request.headers().referer().get()));
            
            try {
                if (!accessToken.equalsIgnoreCase(AccessTokenParam)) {
                    response.status(404).send("{\"reason\":\"unauthorized\"}");
                }
            } catch (Exception e) {
                response.status(404).send("{\"reason\":\"unauthorized\"}");
            }
        }
		try {
            Map<String, DailyStatus>  status = statusService.getRecentStatusMap();
			if(status!=null) {
				response.status(200).send(status);
			} else {
				throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500.addMessageParams("Failed to get status");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
            logger.error("Unable to get status",e);
			request.next(e);
		} 
    }

    @SuppressWarnings("checkstyle:designforextension")
    public void showEnvironment(final ServerRequest request, final ServerResponse response){
        if (accessToken != null && accessToken.length() > 0) {
            String AccessTokenParam = request.queryParams().first(ACCESS_TOKEN_PARAM_NAME).
                    orElseGet(() -> getAccessTokenInReferer(request.headers().referer().get()));

            try {
                if (!accessToken.equalsIgnoreCase(AccessTokenParam)) {
                    response.status(404).send("{\"reason\":\"unauthorized\"}");
                }
            } catch (Exception e) {
                response.status(404).send("{\"reason\":\"unauthorized\"}");
            }
        }
        try {
            Environment env = environmentConfig.getEnvironment();
            if(env!=null) {
                response.status(200).send(env);
            } else {
                throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500.addMessageParams("Failed to get environment");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to get status",e);
            request.next(e);
        }
    }

    @SuppressWarnings("checkstyle:designforextension")
    public void showUserSessionStatusOptionHeaders(final ServerRequest request, final ServerResponse response) {
        response.status(200).headers().add("Content-Type: application/json"
                , "Access-Control-Allow-Origin: *"
                , "Access-Control-Allow-Methods: GET, OPTIONS"
                , "Access-Control-Allow-Headers: *"
                , "Access-Control-Allow-Credentials: true");
    }

    public StatusService getStatusService() {
        return statusService;
    }
}

