package no.cantara.tools.stats.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "services"
})
public class Environment {

    @JsonProperty("name")
    private String name;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Environment withName(String name) {
        this.name = name;
        return this;
    }


    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Environment withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
