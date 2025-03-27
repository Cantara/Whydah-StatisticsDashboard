package no.cantara.tools.stats;

import io.helidon.media.jackson.JacksonSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.cors.CorsSupport;
import io.helidon.webserver.cors.CrossOriginConfig;
import io.helidon.webserver.staticcontent.StaticContentSupport;
import kong.unirest.Unirest;
import no.cantara.config.ApplicationProperties;
import no.cantara.tools.stats.exception.GlobalExceptionHandler;
import no.cantara.tools.stats.status.StatusResource;
import no.cantara.tools.stats.status.StatusService;

import no.cantara.tools.stats.utils.EnvironmentConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * The application main class.
 */
public final class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private final String accessToken;
    private final String applicationInstanceName = "Whydah-StatisticsDashboard";
    private final Instant server_started = Instant.now();
    private final StatusResource statusResource;
    private final HealthResource healthResource;

    private final EnvironmentConfig environmentConfig;

    /**
     * Cannot be instantiated.
     */
    public Main(String accessToken) {
        this.accessToken = accessToken;
        statusResource = new StatusResource(accessToken);
        healthResource = new HealthResource();
        environmentConfig = new EnvironmentConfig();
    }

    /**
     * Application main entry point.
     *
     * @param args command line arguments
     * @throws IOException if there are problems reading logging properties
     */
    public static void main(final String[] args) throws IOException {
  
        setupLogging();

        ApplicationProperties.builder().defaults().buildAndSetStaticSingleton();
        int portNo = ApplicationProperties.getInstance().asInt("server.port", 8080);

        String accessToken = ApplicationProperties.getInstance().get("server.accessToken");

        Main main = new Main(accessToken);

        main.startServer(portNo, getConfiguredContextPath());
    }

    public static String getConfiguredContextPath() {
        String serverContextPath = ApplicationProperties.getInstance().get("server.context-path", "");
        String contextPath = serverContextPath.endsWith("/") ? serverContextPath.substring(0, serverContextPath.length() - 1) : serverContextPath;
        return contextPath;
    }

    public WebServer startServer(int port, String contextPath) {
        String favicon = "/nuxt-spa/dist/%s".formatted(environmentConfig.getEnvironmentFavicon());
    	CorsSupport corsSupport = CorsSupport.builder()
                .addCrossOrigin(CrossOriginConfig.builder()
                            .allowOrigins("http://localhost:3000",
                            		"http://localhost:8088")
                            .allowMethods("*")
                            .build())
                .addCrossOrigin(CrossOriginConfig.create())
                .build();
        Routing routing = Routing.builder()
        		.register(corsSupport)
                .register(contextPath, healthResource)
                .register(contextPath, statusResource)
                .register(contextPath + "/favicon.ico", StaticContentSupport.builder("/nuxt-spa/dist/%s".formatted(favicon))
                        .build())
                .register(contextPath + "/nuxt-spa", StaticContentSupport.builder("/nuxt-spa")
                        .build())
                .register(contextPath + "/_nuxt", StaticContentSupport.builder("/nuxt-spa/dist/_nuxt")
                        .build())
                .register(contextPath + "/", StaticContentSupport.builder("/nuxt-spa/dist")
                        .welcomeFileName("index.html")
                        .build())
                .error(Throwable.class, GlobalExceptionHandler.handleErrors(ApplicationProperties.getInstance().get("app.errorlevel", "0")))
                .build();

        try {
            Unirest.config().connectTimeout(60000);
        } catch (Exception e){
            log.warn("Unable to set timeout",e);
        }

        WebServer ws = WebServer.builder()
                .port(port)
                .addMediaSupport(JacksonSupport.create())
                .routing(routing)
                .build();

        // start the server
        //Server server = startServer();
        ws.start()
                .thenApply(webServer -> {
                            if (accessToken == null || accessToken.length() < 1) {
                                String endpoint = "http://localhost:" + webServer.port() + contextPath;
                                System.out.println("- Visit Dashboard at: " + endpoint);
                                System.out.println(" - Health checks available on: " + endpoint + "/health");
                                System.out.println(" - Environment status available on:  " + endpoint + "/status/");
                            } else {
                                String endpoint = "http://localhost:" + webServer.port() + contextPath;
                                System.out.println(" Visit Dashboard at: " + endpoint + "/?accessToken=" + accessToken);
                                System.out.println(" - Health checks available on: " + endpoint + "/health");
                                System.out.println(" - Environment status available on:  " + endpoint + "/status/?accessToken=" + accessToken);

                            }
                            return null;
                        }
                );
        return ws;
    }

    /**
     * Configure logging from logging.properties file.
     */
    private static void setupLogging() throws IOException {

        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        //Enable openness in JerseyApplication logging.
        LogManager.getLogManager().getLogger("").setLevel(Level.FINEST);
//        try (InputStream is = Main.class.getResourceAsStream("/logging.properties")) {
//            LogManager.getLogManager().readConfiguration(is);
//        }
    }

   
    public StatusResource getStatusResource() {
        return statusResource;
    }

    public HealthResource getHealthResource() {
        return healthResource;
    }
}
