package io.github.pointertrace.siglet.impl.test.bundle.jatjar.suffix.config;


import io.github.pointertrace.siglet.api.SigletConfigParserFactory;
import io.github.pointertrace.siglet.parser.impl.schema.SchemaPropertyBuilder;

import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.property;
import static io.github.pointertrace.siglet.parser.SchemaBuilder.string;


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
