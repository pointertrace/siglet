package io.github.pointertrace.siglet.container.example.bundle.springboot.suffix.parser;

import io.github.pointertrace.siglet.container.example.bundle.springboot.suffix.siglet.SuffixSpanletConfig;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.NodeCheckerFactory;
import org.springframework.stereotype.Component;

import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.*;

@Component
public class SuffixConfigChecker implements NodeCheckerFactory {

    @Override
    public NodeChecker create() {
        return strictObject(SuffixSpanletConfig::new,
                requiredProperty(SuffixSpanletConfig::setSuffix,"suffix",text()));
    }
}
