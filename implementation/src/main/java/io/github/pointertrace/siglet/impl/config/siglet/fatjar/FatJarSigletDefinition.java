package io.github.pointertrace.siglet.impl.config.siglet.fatjar;

import io.github.pointertrace.siglet.api.Siglet;
import io.github.pointertrace.siglet.api.SigletConfigFactory;
import io.github.pointertrace.siglet.api.SigletConfigParserFactory;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.impl.config.siglet.SpanletContextClassloaderSetter;
import io.github.pointertrace.siglet.impl.config.siglet.configfile.SigletConfigFile;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;

import static org.joor.Reflect.onClass;

public class FatJarSigletDefinition implements SigletDefinition {

    private final ClassLoader classLoader;

    private final SigletConfigFile.SigletConfigFileDefinition sigletConfigFileDefinition;

    private final Class<? extends Siglet<?>> processorClass;

    private final Class<? extends SigletConfigParserFactory<?>> configParserFactoryClass;

    private final Class<? extends SigletConfigFactory<?>> configFactoryClass;

    public FatJarSigletDefinition(ClassLoader classLoader, SigletConfigFile.SigletConfigFileDefinition sigletConfigFileDefinition) {
        this.classLoader = classLoader;
        this.sigletConfigFileDefinition = sigletConfigFileDefinition;
        this.processorClass = getProcessorClass();
        this.configFactoryClass = getConfigFactoryClass();
        this.configParserFactoryClass = getConfigParserFactoryClass();
    }

    @Override
    public String getName() {
        return sigletConfigFileDefinition.getName().getValue();
    }

    @SuppressWarnings("unchecked")
    private Class<? extends SigletConfigParserFactory<?>> getConfigParserFactoryClass() {
        if (sigletConfigFileDefinition.getConfigParserFactoryClassName() != null &&
                sigletConfigFileDefinition.getConfigParserFactoryClassName().getValue() != null) {
            return (Class<? extends SigletConfigParserFactory<?>>) getClass(
                    sigletConfigFileDefinition.getConfigParserFactoryClassName().getValue(),
                    SigletConfigParserFactory.class
            );
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends SigletConfigFactory<?>> getConfigFactoryClass() {
        if (sigletConfigFileDefinition.getConfigFactoryClassName() != null &&
                sigletConfigFileDefinition.getConfigFactoryClassName().getValue() != null) {
            return (Class<? extends SigletConfigFactory<?>>) getClass(
                    sigletConfigFileDefinition.getConfigFactoryClassName().getValue(),
                    SigletConfigFactory.class
            );
        } else {
            return null;
        }
    }

    public Spanlet<?> createProcessor() {
        Siglet<?> sigletInstance = createInstance(processorClass);
        if (sigletInstance instanceof Spanlet<?> spanlet) {
            return SpanletContextClassloaderSetter.addContextClassloaderSetter(spanlet, classLoader);
        }
        throw new SigletError("At these point only spanlets are allowed!");
    }

    public ConfigurationFactory<?> createConfigurationFactory() {
        if (configParserFactoryClass != null) {
            return  ConfigurationFactory.of((SigletConfigParserFactory<?>) createInstance(configParserFactoryClass));
        } else if (configFactoryClass != null) {
            return  ConfigurationFactory.of((SigletConfigFactory<?>) createInstance(configFactoryClass));
        } else {
            return ConfigurationFactory.of();
        }
    }


    @SuppressWarnings("unchecked")
    private synchronized Class<? extends Siglet<?>> getProcessorClass() {
        String className = sigletConfigFileDefinition.getSigletClassName().getValue();
        try {
            Class<?> clazz = onClass(className, classLoader).get();
            checkType(clazz, Spanlet.class);
            return (Class<? extends Siglet<?>>) clazz.asSubclass(Siglet.class);
        } catch (Exception e) {
            throw new SigletError((String.format("Class %s not found in jar %s", className, classLoader.toString())), e);
        }
    }

    private <T> Class<? extends T> getClass(String className, Class<T> desiredType) {
        try {
            Class<?> clazz = onClass(className, classLoader).get();
            checkType(clazz, desiredType);
            return clazz.asSubclass(desiredType);
        } catch (Exception e) {
            throw new SigletError(String.format("Class %s not found in jar %s", className, classLoader.toString()), e);
        }
    }

    private <T> T createInstance(Class<? extends T> clazz) {
        try {
            return onClass(clazz).create().get();
        } catch (Exception e) {
            throw new SigletError(String.format("Error creating instance of %s:%s", clazz.getName(), e.getMessage()), e);
        }
    }

    private void checkType(Class<?> clazz, Class<?> desiredType) {
        if (!desiredType.isAssignableFrom(clazz)) {
            throw new SigletError((String.format("Class %s does not implements %s", clazz.getName(), desiredType.getName())));
        }
    }



}
