package io.github.pointertrace.siglet.container.config.siglet;

import io.github.pointertrace.siglet.api.signal.trace.Spanlet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SpanletContextClassloaderSetter implements InvocationHandler {

    private final Spanlet<?> spanlet;

    private final ClassLoader classLoader;

    public SpanletContextClassloaderSetter(Spanlet<?> spanlet, ClassLoader classLoader) {
        this.spanlet = spanlet;
        this.classLoader = classLoader;
    }

    public static <T> Spanlet<T> addContextClassloaderSetter(Spanlet<T> original, ClassLoader classLoader) {
        Object o =  Proxy.newProxyInstance(Spanlet.class.getClassLoader(), new Class<?>[]{Spanlet.class},
                new SpanletContextClassloaderSetter(original, classLoader));
        return (Spanlet<T>) o;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            return method.invoke(spanlet, args);
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }

    public Spanlet<?> getSpanlet() {
        return spanlet;
    }
}
