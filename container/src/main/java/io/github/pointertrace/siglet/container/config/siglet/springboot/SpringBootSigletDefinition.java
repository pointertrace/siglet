package io.github.pointertrace.siglet.container.config.siglet.springboot;

import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.api.Processor;
import io.github.pointertrace.siglet.container.config.graph.SignalType;
import io.github.pointertrace.siglet.container.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletConfig;

public class SpringBootSigletDefinition implements SigletDefinition {

    private final SpringBootContextProxy springBootContextProxy;

    private final SigletConfig sigletConfig;

    public SpringBootSigletDefinition(SpringBootContextProxy springBootContextProxy, SigletConfig sigletConfig) {
        this.springBootContextProxy = springBootContextProxy;
        this.sigletConfig = sigletConfig;
    }

    @Override
    public SigletConfig getSigletConfig() {
        return sigletConfig;
    }

    @Override
    public Processor createProcessor() {
        return springBootContextProxy.getProcessor(sigletConfig.sigletClassName());
    }

    @Override
    public NodeChecker createConfigChecker() {
        return springBootContextProxy.getNodeCheckerFactory(sigletConfig.configCheckerFactoryClassName()).create();
    }

    @Override
    public SignalType getSignalType() {
        Processor processor = createProcessor();
        if (processor instanceof Spanlet<?>) {
            return SignalType.SPAN;
        } else {
            throw new SigletError(String.format("%s is not a spanlet",processor.getClass().getName()));
        }
    }
}
