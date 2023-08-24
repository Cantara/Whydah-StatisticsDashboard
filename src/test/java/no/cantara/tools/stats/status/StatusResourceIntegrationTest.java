package no.cantara.tools.stats.status;

import io.helidon.webserver.WebServer;
import no.cantara.config.ApplicationProperties;
import no.cantara.config.testsupport.ApplicationPropertiesTestHelper;
import no.cantara.tools.stats.Main;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatusResourceIntegrationTest {

    static {
        ApplicationPropertiesTestHelper.enableMutableSingleton();
        ApplicationProperties.builder().testDefaults().buildAndSetStaticSingleton();
    }

    private static Main main;
    private static WebServer server;
    private static String contextPath;

    @BeforeAll
    public static void startTheServer() throws Exception {
        main = new Main("");
        contextPath = Main.getConfiguredContextPath();
        server = main.startServer(0, contextPath);
        server.start().await(5, TimeUnit.SECONDS);
    }

    @AfterAll
    public static void shutdownTheServer() {
        server.shutdown();
    }

    @Test()
    public void testMockedServer() {
       
        try {
            Client client = ClientBuilder.newClient();

            Response jsonObject = client
                    .target(getConnectionString("/status"))
                    .request()
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            assertEquals(200, jsonObject.getStatus(), "GET health status code");
            assertTrue(jsonObject.getEntity().toString().length() > 5);


            Response r = client
                    .target(getConnectionString("/health"))
                    .request()
                    .get();
            assertEquals(200, r.getStatus(), "GET health status code");
        } catch (Exception e) {
            System.out.println(Arrays.asList(e.getStackTrace()));
        }
    }

    private String readResourceAsString(String path) throws IOException {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(path)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String getConnectionString(String path) {
        return "http://localhost:" + server.port() + contextPath + path;
    }
}

