package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ObjectConfigNode;
import com.siglet.config.parser.node.ValueSetter;

import java.util.function.BiConsumer;

public class DynamicPropertyChecker extends BasicPropertyChecker {

    private final DynamicCheckerDiscriminator discriminator;


    public <T, E> DynamicPropertyChecker(String name, boolean required, DynamicCheckerDiscriminator discriminator) {
        super(ValueSetter.EMPTY, name, required);
        this.discriminator = discriminator;
    }

    @Override
    public void check(ConfigNode node) throws SchemaValidationException {
        NodeChecker check = discriminator.getChecker(node);
        check.check(node);
    }

    @Override
    public String getName() {
        return "dynamic property";
    }
}
