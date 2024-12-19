package com.siglet.config.located;

import org.yaml.snakeyaml.nodes.Node;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public interface Location extends Serializable {

    int getLine();

    int getColumn();

    static Location of(int line, int column) {
        return new LocationImpl(line, column);
    }

    static Location of(Node node) {
        return new LocationImpl(node.getStartMark().getLine() + 1, node.getStartMark().getColumn() + 1);
    }

    class LocationImpl implements Location {

        @Serial
        private static final long serialVersionUID = 1834557946117632860L;

        private final int line;

        private final int column;

        private LocationImpl(int line, int column) {
            this.line = line;
            this.column = column;
        }

        @Override
        public int getLine() {
            return line;
        }

        @Override
        public int getColumn() {
            return column;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocationImpl location = (LocationImpl) o;
            return line == location.line && column == location.column;
        }

        @Override
        public int hashCode() {
            return Objects.hash(line, column);
        }

        @Override
        public String toString() {
            return "Location{" +
                    "line=" + line +
                    ", column=" + column +
                    '}';
        }
    }
}