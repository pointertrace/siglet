package io.github.pointertrace.siglet.container.example.bundle.springboot.metricfromspan.parser;

import io.github.pointertrace.siglet.api.SigletConfigFactory;
import io.github.pointertrace.siglet.container.example.bundle.springboot.metricfromspan.siglet.MetricFromSpanConfig;
import org.springframework.stereotype.Component;

@Component
public class MetricFromSpanConfigFactory implements SigletConfigFactory<MetricFromSpanConfig> {

    @Override
    public MetricFromSpanConfig createConfig(String yaml) {
        if (yaml == null || !yaml.startsWith("extra: ")) {
            throw new IllegalArgumentException("invalid yaml configuration - expecting a 'extra' object");
        }
        MetricFromSpanConfig config = new MetricFromSpanConfig();
        config.setExtra(yaml.substring("extra: ".length()));
        return config;
    }
}
