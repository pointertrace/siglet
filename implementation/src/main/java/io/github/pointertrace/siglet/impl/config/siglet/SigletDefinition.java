package io.github.pointertrace.siglet.impl.config.siglet;

import io.github.pointertrace.siglet.api.Siglet;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.config.siglet.parser.SigletConfig;
import io.github.pointertrace.siglet.parser.NodeChecker;

public interface SigletDefinition {
    
    SigletConfig getSigletConfig();

    Siglet createProcessor();

    NodeChecker createConfigChecker();

    SignalType getSignalType();

}
