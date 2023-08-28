package no.cantara.tools.stats.status;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import no.cantara.tools.stats.domain.DailyStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import no.cantara.tools.stats.domain.UserSessionStatus;
import no.cantara.tools.stats.exception.AppExceptionCode;
import no.cantara.tools.stats.domain.UserSessionStatus;

public class StatusResource implements Service {

    private static final Logger logger = LoggerFactory.getLogger(StatusResource.class);

    private static final String ACCESS_TOKEN_PARAM_NAME = "accessToken";

    final StatusService statusService = new StatusService();

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
        .options("/status", this::showUserSessionStatusOptionHeaders);
    }
    
    @SuppressWarnings("checkstyle:designforextension")
    public void showUserSessionStatus(final ServerRequest request, final ServerResponse response){
        if (accessToken != null && accessToken.length() > 0) {
            Optional<String> AccessTokenParam = request.queryParams().first(ACCESS_TOKEN_PARAM_NAME);
            try {
                if (!accessToken.equalsIgnoreCase(AccessTokenParam.get())) {
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

