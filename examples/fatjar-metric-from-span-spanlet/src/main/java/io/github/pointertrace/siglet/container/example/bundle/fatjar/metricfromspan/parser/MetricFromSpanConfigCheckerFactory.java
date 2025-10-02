package io.github.pointertrace.siglet.container.example.bundle.fatjar.metricfromspan.parser;

import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.NodeCheckerFactory;

/**
 * Factory that would create a {@link NodeChecker} for parsing a siglet's configuration.
 * This siglet does not define any configuration, so this factory returns {@code null}.
 *
 * @see NodeChecker
 * @see NodeCheckerFactory
 */
public class MetricFromSpanConfigCheckerFactory implements NodeCheckerFactory {

    /**
     * Returns the {@link NodeChecker} responsible for parsing the siglet configuration.
     * Since this siglet has no configuration, this implementation always returns {@code null}.
     *
     * @return {@code null}, indicating that there is no configuration to parse for this siglet.
     */
    @Override
    public NodeChecker create() {
        return null;
    }
}