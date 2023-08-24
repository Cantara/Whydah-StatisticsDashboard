package no.cantara.tools.stats.exception;

public class AppExceptionCode {

	
	//COMMON
	public static AppException COMMON_INTERNALEXCEPTION_500 = new AppException(500, 9999, "Internal server exception: %s", "","");
	public static AppException COMMON_BADREQUESTEXCEPTION_500 = new AppException(500, 9998, "Bad request: %s", "","");
	public static AppException COMMON_IDNOTFOUNDEXCEPTION_404 = new AppException(404, 9997, "Id not found: %s", "","");
	
}
