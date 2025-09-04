package io.github.pointertrace.siglet.container.config.siglet;

import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.api.Processor;
import io.github.pointertrace.siglet.container.config.graph.SignalType;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletConfig;

public interface SigletDefinition {
    
    SigletConfig getSigletConfig();

    Processor createProcessor();

    NodeChecker createConfigChecker();

    SignalType getSignalType();

}
