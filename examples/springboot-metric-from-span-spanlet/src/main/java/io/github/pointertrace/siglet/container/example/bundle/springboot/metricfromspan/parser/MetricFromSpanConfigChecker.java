package io.github.pointertrace.siglet.container.example.bundle.springboot.metricfromspan.parser;

import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.NodeCheckerFactory;
import org.springframework.stereotype.Component;

@Component
public class MetricFromSpanConfigChecker implements NodeCheckerFactory {

    @Override
    public NodeChecker create() {
        return null;
    }
}
