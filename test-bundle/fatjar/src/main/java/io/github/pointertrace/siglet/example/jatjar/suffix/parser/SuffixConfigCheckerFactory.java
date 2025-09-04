package io.github.pointertrace.siglet.example.jatjar.suffix.parser;


import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.NodeCheckerFactory;
import io.github.pointertrace.siglet.example.jatjar.suffix.siglet.SuffixSpanletConfig;

import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.*;


public class SuffixConfigCheckerFactory implements NodeCheckerFactory {


    @Override
    public NodeChecker create() {
        return strictObject(SuffixSpanletConfig::new,
                requiredProperty(SuffixSpanletConfig::setSuffix,"suffix",text()));
    }
}
