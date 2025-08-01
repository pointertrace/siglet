package io.github.pointertrace.siglet.container.eventloop.processor.result;

import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.engine.SignalDestination;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ResultImpl implements Result {


    public static ResultImpl drop() {
        return new ResultImpl(SignalRoute.drop());
    }

    public static ResultImpl proceed() {
        return new ResultImpl(SignalRoute.proceed());
    }

    public static ResultImpl proceed(String destination) {
        return new ResultImpl(SignalRoute.proceed(destination));
    }

    List<SignalRoute> routes = new ArrayList<>();

    private ResultImpl(SignalRoute route) {
        routes.add(route);
    }

    @Override
    public Result andSend(Signal signal, String destination) {
        this.routes.add(SignalRoute.route(signal,destination));
        return this;
    }

    public <T extends Signal> void dispatch(Map<String,String> destinationMappings, T processSignal,
                                            List<SignalDestination> availableDestinations) {
        for(SignalRoute route : routes) {
            route.dispatch(destinationMappings, processSignal, availableDestinations);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ResultImpl result = (ResultImpl) o;
        return Objects.equals(routes, result.routes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(routes);
    }
}

