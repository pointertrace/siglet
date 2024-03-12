package com.siglet.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GlobalConfig {

    private final List<Endpoint> endpoints;

    private final Map<String,Object> properties;

    private final List<Pipeline> pipelines;

    public GlobalConfig(List<Endpoint> endpoints, Map<String, Object> properties, List<Pipeline> pipelines) {
        this.endpoints = Collections.unmodifiableList(endpoints);
        this.properties = Collections.unmodifiableMap(properties);
        this.pipelines = Collections.unmodifiableList(pipelines);
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public Map<String,Object> getProperties() {
        return properties;
    }

    public List<Pipeline> getPipelines() {
        return pipelines;
    }

}
