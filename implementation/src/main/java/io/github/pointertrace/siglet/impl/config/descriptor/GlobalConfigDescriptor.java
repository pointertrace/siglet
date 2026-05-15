package io.github.pointertrace.siglet.impl.config.descriptor;


import io.github.pointertrace.siglet.parser.IntegerValue;
import io.github.pointertrace.siglet.parser.Locatable;
import io.github.pointertrace.siglet.parser.Location;
import io.github.pointertrace.siglet.parser.Schema;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;

public class GlobalConfigDescriptor implements Locatable {

    private Location location;

    private IntegerValue queueSize;

    private IntegerValue threadPoolSize;

    public IntegerValue getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(IntegerValue queueSize) {
        this.queueSize = queueSize;
    }

    public IntegerValue getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(IntegerValue threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public static Schema.Builder<?, GlobalConfigDescriptor> descriptorSchemaBuilder() {
        return object(GlobalConfigDescriptor::new)
                .addProperty(property("queue-size", GlobalConfigDescriptor::setQueueSize, integerValueObject()))
                .addProperty(property("thread-pool-size", GlobalConfigDescriptor::setThreadPoolSize, integerValueObject()));

    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
