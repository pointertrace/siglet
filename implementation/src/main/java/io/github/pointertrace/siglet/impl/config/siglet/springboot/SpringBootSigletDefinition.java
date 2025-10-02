package io.github.pointertrace.siglet.impl.config.siglet.springboot;

import io.github.pointertrace.siglet.api.Siglet;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.impl.config.siglet.parser.SigletConfig;
import io.github.pointertrace.siglet.parser.NodeChecker;

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
    public Siglet createProcessor() {
        return springBootContextProxy.getProcessor(sigletConfig.sigletClassName());
    }

    @Override
    public NodeChecker createConfigChecker() {
        return springBootContextProxy.getNodeCheckerFactory(sigletConfig.configCheckerFactoryClassName()).create();
    }

    @Override
    public SignalType getSignalType() {
        Siglet siglet = createProcessor();
        if (siglet instanceof Spanlet<?>) {
            return SignalType.SPAN;
        } else {
            throw new SigletError(String.format("%s is not a spanlet", siglet.getClass().getName()));
        }
    }
}
