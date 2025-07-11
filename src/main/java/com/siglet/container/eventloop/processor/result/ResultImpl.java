package com.siglet.container.eventloop.processor.result;

import com.siglet.api.Result;
import com.siglet.api.Signal;
import com.siglet.container.engine.SignalDestination;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
                                            List<SignalDestination<T>> availableDestinations) {
        for(SignalRoute route : routes) {
            route.dispatch(destinationMappings, processSignal, availableDestinations);
        }
    }
}

