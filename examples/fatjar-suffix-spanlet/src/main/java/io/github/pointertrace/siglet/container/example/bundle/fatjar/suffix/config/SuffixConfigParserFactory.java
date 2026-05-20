package io.github.pointertrace.siglet.container.example.bundle.fatjar.suffix.config;


import io.github.pointertrace.siglet.api.SigletConfigParserFactory;
import io.github.pointertrace.siglet.parser.impl.schema.SchemaPropertyBuilder;

import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.property;
import static io.github.pointertrace.siglet.parser.SchemaBuilder.string;


/**
 * Implementation of {@Link SigletConfigParserFactory} that specifies how the yaml configuration for this siglet
 * will be parsed, validated and how the configuration will be mapped to the siglet configuration object.
 *
 * The parser config factory produced by this factory:
 * - Requires a valid "suffix" property.
 * - Verifies the "suffix" property using a text validator.
 * - Maps the validated property to the {@link SuffixSpanletConfig} object through
 *   its {@link SuffixSpanletConfig#setSuffix(String)} method.
 *
 * The creation of the checker utilizes a strict validation mode to enforce
 * precise schema adherence.
 */
public class SuffixConfigParserFactory implements SigletConfigParserFactory<SuffixSpanletConfig> {

    @Override
    public List<SchemaPropertyBuilder<SuffixSpanletConfig, ?>> createConfigSchema() {
        return List.of(
                property("suffix", SuffixSpanletConfig::setSuffix, string())
        );
    }

    @Override
    public Class<SuffixSpanletConfig> getConfigClass() {
        return SuffixSpanletConfig.class;
    }
}
