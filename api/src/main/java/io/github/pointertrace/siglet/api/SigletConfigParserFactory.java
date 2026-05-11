package io.github.pointertrace.siglet.api;

import io.github.pointertrace.siglet.parser.impl.schema.SchemaPropertyBuilder;

import java.util.List;

public interface SigletConfigParserFactory<T> {

    List<SchemaPropertyBuilder<T, ?>> createConfigSchema();

    Class<T> getConfigClass();
}
