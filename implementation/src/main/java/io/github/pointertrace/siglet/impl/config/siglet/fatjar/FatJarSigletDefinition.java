package io.github.pointertrace.siglet.impl.config.siglet.fatjar;

import io.github.pointertrace.siglet.api.Siglet;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.impl.config.siglet.SpanletContextClassloaderSetter;
import io.github.pointertrace.siglet.impl.config.siglet.parser.SigletConfig;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.NodeCheckerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class FatJarSigletDefinition implements SigletDefinition {

    private final ClassLoader classLoader;

    private final SigletConfig sigletConfig;

    private Class<? extends Siglet> processorClass;

    private Class<NodeCheckerFactory> configCheckerFactoryClass;


    public FatJarSigletDefinition(ClassLoader classLoader, SigletConfig sigletConfig) {
        this.classLoader = classLoader;
        this.sigletConfig = sigletConfig;
    }

    @Override
    public SigletConfig getSigletConfig() {
        return sigletConfig;
    }

    @Override
    public Siglet createProcessor() {
        try {
            Siglet sigletInstance =
                    getNonArgumentConstructor(getProcessorClass()).newInstance();
            if (sigletInstance instanceof Spanlet<?> spanlet) {
                return SpanletContextClassloaderSetter.addContextClassloaderSetter(spanlet, classLoader);
            }
            throw new SigletError("At these point only spanlets are allowed!");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new SigletError(String.format("Error creating instance of siglet %s. %s,",
                    getProcessorClass().getName(), e.getMessage()), e);
        }
    }

    private synchronized Class<? extends Siglet> getProcessorClass() {
        if (processorClass == null) {
            try {
                Class<?> clazz = Class.forName(sigletConfig.sigletClassName(), true, classLoader);
                checkType(clazz, Spanlet.class);
                processorClass = clazz.asSubclass(Siglet.class);
            } catch (ClassNotFoundException e) {
                throw new SigletError((String.format("Class %s not found in jar %s", sigletConfig.sigletClassName(),
                        classLoader.toString())));
            }
        }
        return processorClass;
    }

    private <T> Constructor<T> getNonArgumentConstructor(Class<T> clazz) {
        return (Constructor<T>) Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 0 && Modifier.isPublic(c.getModifiers()))
                .findAny()
                .orElseThrow(() -> new SigletError(String.format("Class %s does not have a non-args public " +
                                                                 "constructor", clazz.getName())));
    }

    @Override
    public NodeChecker createConfigChecker() {
        try {
            return getNonArgumentConstructor(getConfigCheckerFactoryClass()).newInstance().create();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new SigletError(String.format("Error creating instance of node checker factory %s. %s,",
                    getConfigCheckerFactoryClass().getName(), e.getMessage()), e);
        }
    }

    private synchronized Class<NodeCheckerFactory> getConfigCheckerFactoryClass() {
        if (configCheckerFactoryClass == null) {
            try {
                Class<?> clazz = Class.forName(sigletConfig.configCheckerFactoryClassName(), true, classLoader);
                checkType(clazz, NodeCheckerFactory.class);
                configCheckerFactoryClass = (Class<NodeCheckerFactory>) clazz;
            } catch (ClassNotFoundException e) {
                throw new SigletError((String.format("Class %s not found in jar %s", sigletConfig.sigletClassName(),
                        classLoader.toString())));
            }
        }
        return configCheckerFactoryClass;
    }

    private void checkType(Class<?> clazz, Class<?> desiredType) {
        if (!desiredType.isAssignableFrom(clazz)) {
            throw new SigletError((String.format("Class %s does not implements %s", clazz.getName(), desiredType.getName())));
        }
    }

    @Override
    public SignalType getSignalType() {
        if (Spanlet.class.isAssignableFrom(getProcessorClass())) {
            return SignalType.SPAN;
        } else {
            throw new SigletError(String.format("%s is not a spanlet", getProcessorClass()));
        }
    }

}
