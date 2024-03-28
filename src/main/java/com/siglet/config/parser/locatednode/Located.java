package com.siglet.config.parser.locatednode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface Located {

    Location getLocation();

    class LocatedArrayNode extends ArrayNode implements Located {

        private final Location location;


        public LocatedArrayNode(JsonNodeFactory nc, Location location) {
            super(nc);
            this.location = location;
        }

        public LocatedArrayNode(JsonNodeFactory nc, int capacity, Location location) {
            super(nc, capacity);
            this.location = location;
        }

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public JsonNode get(int index) {
            return super.get(index);
        }
    }

    class LocatedBigDecimalNode extends DecimalNode implements Located {

        private final Location location;

        public LocatedBigDecimalNode(BigDecimal value, Location location) {
            super(value);
            this.location = location;
        }

        @Override
        public Location getLocation() {
            return location;
        }


    }

    class LocatedBigIntegerNode extends BigIntegerNode implements Located {


        private final Location location;

        public LocatedBigIntegerNode(BigInteger value, Location location) {
            super(value);
            this.location = location;
        }

        @Override
        public Location getLocation() {
            return location;
        }

    }

    class LocatedBinaryNode extends BinaryNode implements Located {

        private final Location location;

        public LocatedBinaryNode(byte[] value, int offset, int length, Location location) {
            super(value, offset, length);
            this.location = location;
        }

        public LocatedBinaryNode(byte[] value, Location location) {
            this(value, 0, value.length, location);
        }


        @Override
        public Location getLocation() {
            return location;
        }


    }

    class LocatedNullNode extends NullNode implements Located {

        private final Location location;


        public LocatedNullNode(Location location) {
            this.location = location;
        }

        @Override
        public Location getLocation() {
            return location;
        }

    }

    class LocatedBooleanNode extends BooleanNode implements Located {

        private final Location location;

        protected LocatedBooleanNode(boolean v, Location location) {
            super(v);
            this.location = location;
        }


        @Override
        public Location getLocation() {
            return location;
        }

    }

    class LocatedIntNode extends IntNode implements Located {


        private final Location location;

        public LocatedIntNode(int value, Location location) {
            super(value);
            this.location = location;
        }

        @Override
        public Location getLocation() {
            return location;
        }

    }

    class LocatedLongNode extends LongNode implements Located {


        private final Location location;

        public LocatedLongNode(long value, Location location) {
            super(value);
            this.location = location;
        }

        @Override
        public Location getLocation() {
            return location;
        }

    }

    class LocatedObjectNode extends ObjectNode implements Located {


        private final Location location;

        public LocatedObjectNode(JsonNodeFactory nc, Location location) {
            super(nc);
            this.location = location;
        }

        @Override
        public Location getLocation() {
            return location;
        }

    }

    class LocatedTextNode extends TextNode implements Located {


        private final Location location;

        public LocatedTextNode(String text, Location location) {
            super(text);
            this.location = location;
        }

        @Override
        public Location getLocation() {
            return location;
        }

    }
}

