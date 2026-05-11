package io.github.pointertrace.siglet.impl.test.bundle.springboot.suffix.config;

import io.github.pointertrace.siglet.api.SigletConfigFactory;
import org.springframework.stereotype.Component;

@Component
public class SuffixConfigFactory implements SigletConfigFactory<SuffixSpanletConfig> {


    @Override
    public SuffixSpanletConfig createConfig(String yaml) {
        if (yaml != null && yaml.startsWith("suffix: ")) {
            SuffixSpanletConfig config = new SuffixSpanletConfig();
            config.setSuffix(yaml.substring("suffix: ".length()));
            return config;
        }
        throw new IllegalArgumentException("invalid yaml configuration - expecting a 'suffix' property");
    }
}
