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
        "favicon"
})
public class Environment {

    @JsonProperty("name")
    private String name;

    @JsonProperty("favicon")
    private String favicon;

    @JsonProperty("name")
    public String getName() {
        return name.toUpperCase();
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("favicon")
    public String getFavicon() {
        return favicon;
    }

    @JsonProperty("favicon")
    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }
    public Environment(String name, String favicon) {
        this.name = name;
        this.favicon = favicon;
    }
}
