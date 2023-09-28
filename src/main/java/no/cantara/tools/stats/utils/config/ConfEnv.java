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

    @JsonProperty("environment_name")
    private String environmentName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("environment_name")
    public String getEnvironmentName() {
        return environmentName;
    }

    @JsonProperty("environment_name")
    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
