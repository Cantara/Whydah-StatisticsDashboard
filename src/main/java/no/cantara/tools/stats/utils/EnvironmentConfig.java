package no.cantara.tools.stats.utils;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.cantara.tools.stats.utils.config.ConfEnv;

import no.cantara.tools.stats.domain.Environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class EnvironmentConfig {

    public static final Logger logger = LoggerFactory.getLogger(EnvironmentConfig.class);
    private static ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature())
            .findAndRegisterModules();
    private boolean exists = false;
    private ConfEnv confEnv;
    private String environmentName = "";
    private String environmentFavicon = "";
    private Environment environment;
    private String environmentAsString;
    private final static Set<URI> pollingHealthURISet = new CopyOnWriteArraySet();

    public EnvironmentConfig() {

        if (!exists) {
            ConfEnv readEnvironment = readConfig();
            if (readEnvironment != null) {
                try {
                    setUpEnvironment(readEnvironment);
                    confEnv = readEnvironment;
                    exists = true;
                } catch (Exception e) {
                    exists = false;
                    logger.error("Unable to parse environment", e);
                }
            }
        }
    }

    private void setUpEnvironment(ConfEnv readEnvironment) throws Exception {
        environmentName = readEnvironment.getEnvironmentName();
        environmentFavicon = readEnvironment.getFavicon();
        environment = new Environment(environmentName, environmentFavicon);
        environmentAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(environment);
    }


    public String getEnvironmentAsString() {
        return environmentAsString;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public ConfEnv getConfEnv() {
        return confEnv;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public Environment getDefaultEnvironment() {
        Environment env = new Environment("Statistics Dashboard", "quadim.favicon.ico");
        return env;
    }

    public String getEnvironmentFavicon() {
        return environmentFavicon;
    }
    private ConfEnv readConfig() {
        try (InputStream fileStream = new FileInputStream("./environment_config.json")) {
            ConfEnv envConf = mapper.readValue(fileStream, ConfEnv.class);
            return envConf;
        } catch (Exception e) {
            exists = false;
        }
        return null;
    }
}
