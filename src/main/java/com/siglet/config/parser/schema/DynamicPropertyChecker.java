package com.siglet.config.parser.schema;

import com.siglet.config.located.Located;
import com.siglet.config.located.Location;
import com.siglet.config.parser.node.LocationSetter;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ValueSetter;

import java.util.function.BiConsumer;

public class DynamicPropertyChecker extends BasicPropertyChecker {

    private final DynamicCheckerDiscriminator discriminator;

    public <T, E, R extends Located> DynamicPropertyChecker(String name, BiConsumer<R, Location> locationSetter, boolean required, DynamicCheckerDiscriminator discriminator) {
        super(ValueSetter.EMPTY,LocationSetter.of(locationSetter), name, required);
        this.discriminator = discriminator;
    }

    public <T, E> DynamicPropertyChecker(String name, boolean required, DynamicCheckerDiscriminator discriminator) {
        this(name, null, required, discriminator);
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        NodeChecker check = discriminator.getChecker(node);
        check.check(node);
    }

    @Override
    public String getName() {
        return "dynamic property";
    }

    @Override
    public String getDescription() {
        return String.format("discriminator:%s",discriminator);
    }
}
