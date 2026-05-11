package io.github.pointertrace.siglet.container.example.bundle.springboot.suffix.config;

import io.github.pointertrace.siglet.api.SigletConfigParserFactory;
import io.github.pointertrace.siglet.parser.impl.schema.SchemaPropertyBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.property;
import static io.github.pointertrace.siglet.parser.SchemaBuilder.string;


@Component
public class SuffixConfigParserFactory implements SigletConfigParserFactory<SuffixSpanletConfig> {

    @Override
    public List<SchemaPropertyBuilder<SuffixSpanletConfig, ?>> createConfigSchema() {
        return List.of(property("suffix", SuffixSpanletConfig::setSuffix, string()));
    }

    @Override
    public Class<SuffixSpanletConfig> getConfigClass() {
        return SuffixSpanletConfig.class;
    }
}
