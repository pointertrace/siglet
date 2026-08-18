package io.github.pointertrace.siglet.impl.engine.component.connection;

import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import io.github.pointertrace.siglet.impl.engine.component.SignalReceiverFunction;

public interface SignalDestination {

    String ALL = "__INTERNAL_DESTINATION_ALL__";

    String DROP = "__INTERNAL_DESTINATION_DROP__";

    static boolean isAll(String destination) {
        return ALL.equals(destination);
    }

    static boolean isDrop(String destination) {
        return DROP.equals(destination);
    }

    GraphComponent<?> getGraphComponent();

    SignalReceiverFunction getSignalReceiverFunction();

    default boolean is(String destination) {
        return getGraphComponent().getName().equals(destination);
    }

    default String getName() {
        return getGraphComponent().getName();
    }
}
