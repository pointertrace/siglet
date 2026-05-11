package io.github.pointertrace.siglet.impl.config.siglet.springboot;

import io.github.pointertrace.siglet.api.Siglet;
import io.github.pointertrace.siglet.api.SigletConfigFactory;
import io.github.pointertrace.siglet.api.SigletConfigParserFactory;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.impl.config.siglet.configfile.SigletConfigFile;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;

public class SpringBootSigletDefinition implements SigletDefinition {

    private final SpringBootContextProxy springBootContextProxy;

    private final SigletConfigFile.SigletConfigFileDefinition sigletConfigFileDefinition;

    private final Siglet<?> processor;

    private final SigletConfigParserFactory<?> configParserFactory;

    private final SigletConfigFactory<?> configFactory;

    public SpringBootSigletDefinition(SpringBootContextProxy springBootContextProxy,
                                      SigletConfigFile.SigletConfigFileDefinition sigletConfigFileDefinition) {
        this.springBootContextProxy = springBootContextProxy;
        this.sigletConfigFileDefinition = sigletConfigFileDefinition;
        this.processor = getProcessor();
        this.configFactory = getConfigFactory();
        this.configParserFactory = getConfigParserFactory();
    }

    private SigletConfigParserFactory<?> getConfigParserFactory() {
        if (sigletConfigFileDefinition.getConfigParserFactoryClassName() != null) {
            return springBootContextProxy.getConfigParserFactory(sigletConfigFileDefinition.getConfigParserFactoryClassName().getValue());
        } else {
            return null;
        }
    }

    private SigletConfigFactory<?> getConfigFactory() {
        if (sigletConfigFileDefinition.getConfigFactoryClassName() != null) {
            return springBootContextProxy.getConfigFactory(sigletConfigFileDefinition.getConfigFactoryClassName().getValue());
        } else {
            return null;
        }
    }

    private Siglet<?> getProcessor() {
        return springBootContextProxy.getProcessor(sigletConfigFileDefinition.getSigletClassName().getValue());
    }

    @Override
    public String getName() {
        return sigletConfigFileDefinition.getName().getValue();
    }

    @Override
    public Spanlet<?> createProcessor() {
        return (Spanlet<?>) processor;
    }

    @Override
    public ConfigurationFactory<?> createConfigurationFactory() {
        if (configFactory != null) {
            return ConfigurationFactory.of(configFactory);
        } else if (configParserFactory != null) {
            return ConfigurationFactory.of(configParserFactory);
        } else {
            return ConfigurationFactory.of();
        }
    }

}
