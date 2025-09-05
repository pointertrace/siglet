package io.github.pointertrace.siglet.api;

public interface ResultFactory {

    Result drop();

    Result proceed();

    Result proceed(String destination);

}
