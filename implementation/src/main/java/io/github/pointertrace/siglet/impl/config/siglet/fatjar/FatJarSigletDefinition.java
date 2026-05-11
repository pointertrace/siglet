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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

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
            Class<?> clazz = Class.forName(className, true, classLoader);
            checkType(clazz, Spanlet.class);
            return (Class<? extends Siglet<?>>) clazz.asSubclass(Siglet.class);
        } catch (ClassNotFoundException e) {
            throw new SigletError((String.format("Class %s not found in jar %s", className, classLoader.toString())));
        }
    }

    private Constructor<?> getNonArgumentConstructor(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 0 && Modifier.isPublic(c.getModifiers()))
                .findAny()
                .orElseThrow(() -> new SigletError(String.format("Class %s does not have a non-args public " +
                        "constructor", clazz.getName())));
    }

    private <T> Class<? extends T> getClass(String className, Class<T> desiredType) {
        try {
            Class<?> clazz = Class.forName(className, true, classLoader);
            checkType(clazz, desiredType);
            return clazz.asSubclass(desiredType);
        } catch (ClassNotFoundException e) {
            throw new SigletError(String.format("Class %s not found in jar %s", className, classLoader.toString()));
        }
    }

    private <T> T createInstance(Class<? extends T> clazz) {
        try {
            return clazz.cast(getNonArgumentConstructor(clazz).newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SigletError(String.format("Error creating instance of %s:%s", clazz.getName(), e.getMessage()), e);
        } catch (InvocationTargetException e) {
            throw new SigletError(String.format("Error creating instance of %s:%s", clazz.getName(), e.getTargetException().getMessage()), e.getTargetException());
        }
    }

    private void checkType(Class<?> clazz, Class<?> desiredType) {
        if (!desiredType.isAssignableFrom(clazz)) {
            throw new SigletError((String.format("Class %s does not implements %s", clazz.getName(), desiredType.getName())));
        }
    }



}
