package io.github.pointertrace.siglet.impl.config.siglet;

import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;

public interface SigletDefinition {

    String getName();

    Spanlet<?> createProcessor();

    ConfigurationFactory<?> createConfigurationFactory();
}
