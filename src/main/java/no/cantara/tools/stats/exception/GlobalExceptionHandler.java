package no.cantara.tools.stats.exception;

import io.helidon.common.http.Http;
import io.helidon.webserver.ErrorHandler;
import io.helidon.webserver.NotFoundException;
import no.cantara.tools.stats.utils.EntityUtils;

public class GlobalExceptionHandler {
	public static ErrorHandler<Throwable> handleErrors(String errorlevel) {
		return (req, res, t) -> {
			Throwable root = t;

			while (!(root instanceof AppException) && root.getCause() != null) {
				root = root.getCause();
			}

			if (root instanceof AppException exception) {
				String error_res = ExceptionConfig.handleSecurity(new ErrorMessage(exception), errorlevel).toString();	
				res.status(exception.getStatus()).send(EntityUtils.jsonString_toJsonObject(error_res));
			} else if(root instanceof NotFoundException) {
				res.status(Http.Status.MOVED_PERMANENTLY_301);
				res.headers().put(Http.Header.LOCATION, "/");
				res.send();
			} 
			else {
				req.next(t);
			}
		};
	}


}
