package io.github.pointertrace.siglet.container.config.siglet.springboot;

import io.github.pointertrace.siglet.parser.NodeCheckerFactory;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.api.SigletError;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.joor.Reflect.on;
import static org.joor.Reflect.onClass;

public class SpringBootContextProxy implements Closeable {

    private final SpringBootClassLoader classLoader;

    private final Object springContextBuilder;

    private Object springContext;

    private boolean started = false;

    public SpringBootContextProxy(SpringBootClassLoader classLoader, String startClassName) {
        this.classLoader = classLoader;
        springContextBuilder = createSpringContextBuilder(classLoader, startClassName);
    }

    public Spanlet<?> getProcessor(String className) {
        if (springContext != null && started) {
            return withContextClassLoader(classLoader, className, cn -> {
                Class<?> processorClass = onClass(cn, classLoader).get();
                return on(springContext).call("getBean", processorClass).get();
            });
        } else {
            throw new SigletError("Spring context has not been started yet");
        }
    }

    public NodeCheckerFactory getNodeCheckerFactory(String className) {
        if (springContext != null && started) {
            return withContextClassLoader(classLoader, className, cn -> {
                Class<?> nodeCheckerClass = onClass(cn, classLoader).get();
                return on(springContext).call("getBean", nodeCheckerClass).get();
            });
        } else {
            throw new SigletError("Spring context has not been started yet");
        }
    }

    public void start() {
        if (!started) {
            withContextClassLoader(classLoader, () -> {
                try {
                    springContext = on(springContextBuilder)
                            .call("run", (Object) new String[]{})
                            .get();
                    started = true;
                } catch (Exception e) {
                    throw new SigletError(String.format("Error starting spring context for jar %s: %s",
                            classLoader.getName(), e.getMessage()), e);
                }
            });
        } else {
            throw new SigletError("Siglet already started");
        }
    }

    public void stop() {
        withContextClassLoader(classLoader, () -> {
            try {
                on(springContext).call("close");
            } catch (Exception e) {
                throw new SigletError(String.format("Error stopping spring context for jar %s: %s",
                        classLoader.getName(), e.getMessage()), e);
            }
        });
    }

    @Override
    public void close() {
        List<String> errors = new ArrayList<>();

        try {
            stop();
        } catch (Throwable t) {
            errors.add(String.format("  Error stopping spring classloader for jar %s: %s", classLoader.getName(),
                    t.getMessage()));
        }

        try {
            classLoader.close();
        } catch (Throwable t) {
            errors.add(String.format("  Error closing classloader %s: %s", classLoader.getName(), t.getMessage()));
        }

        if (!errors.isEmpty()) {
            throw new SigletError(String.format("Error(s) closing spring context for jar %s:\n%s", classLoader.getName(),
                    String.join("\n", errors)));
        }
    }

    private Object createSpringContextBuilder(ClassLoader classLoader, String startClassName) {
        return withContextClassLoader(classLoader, startClassName, scn -> {

            Object defaultResourceLoader =
                    onClass("org.springframework.core.io.DefaultResourceLoader", classLoader)
                            .create(classLoader)
                            .get();

            Class<?> startClass = onClass(scn, classLoader).get();

            Object springContextBuilderTmp = onClass("org.springframework.boot.builder.SpringApplicationBuilder",
                    classLoader)
                    .create(defaultResourceLoader, (Object) new Class[]{startClass})
                    .get();

            return on(springContextBuilderTmp).call("properties", Map.of(
                    "spring.main.banner-mode", "off",
                    "logging.level.root", "INFO")
            ).get();
        });
    }

    private void withContextClassLoader(ClassLoader springBootClassLoader, Runnable runnable) {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(springBootClassLoader);
        try {
            runnable.run();
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }


    public static <T, R> R withContextClassLoader(ClassLoader springBootClassLoader, T input, Function<T, R> action) {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(springBootClassLoader);
            return action.apply(input);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

}
