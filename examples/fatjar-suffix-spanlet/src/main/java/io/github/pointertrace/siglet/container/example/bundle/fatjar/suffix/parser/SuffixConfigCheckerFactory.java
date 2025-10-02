package io.github.pointertrace.siglet.container.example.bundle.fatjar.suffix.parser;


import io.github.pointertrace.siglet.container.example.bundle.fatjar.suffix.siglet.SuffixSpanletConfig;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.NodeCheckerFactory;

import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.*;


/**
 * Factory class that produces a {@link NodeChecker} configured specifically for
 * validating suffix configurations represented by {@link SuffixSpanletConfig}.
 * This factory adheres to the {@link NodeCheckerFactory} interface, ensuring conformity
 * with a standard for creating NodeChecker implementations.
 *
 * The checker produced by this factory:
 * - Requires a valid "suffix" property.
 * - Verifies the "suffix" property using a text validator.
 * - Maps the validated property to the {@link SuffixSpanletConfig} object through
 *   its {@link SuffixSpanletConfig#setSuffix(String)} method.
 *
 * The creation of the checker utilizes a strict validation mode to enforce
 * precise schema adherence.
 */
public class SuffixConfigCheckerFactory implements NodeCheckerFactory {


    @Override
    public NodeChecker create() {
        return strictObject(SuffixSpanletConfig::new,
                requiredProperty(SuffixSpanletConfig::setSuffix,"suffix",text()));
    }
}
