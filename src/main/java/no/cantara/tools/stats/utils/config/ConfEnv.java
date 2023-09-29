package no.cantara.tools.stats.utils.config;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "environment_name",
})
public class ConfEnv {

    @JsonProperty("favicon")
    private String favicon;

    @JsonProperty("favicon")
    public String getFavicon() {
        return favicon;
    }
    @JsonProperty("favicon")
    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }
    @JsonProperty("environment_name")
    private String environmentName;

    @JsonProperty("environment_name")
    public String getEnvironmentName() {
        return environmentName;
    }

    @JsonProperty("environment_name")
    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }
}
