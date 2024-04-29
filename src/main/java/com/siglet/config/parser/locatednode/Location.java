package com.siglet.config.parser.locatednode;

import com.fasterxml.jackson.core.JsonParser;
import org.yaml.snakeyaml.error.Mark;

import java.util.Objects;

public interface Location {
    int getLine();

    int getColumn();

    static Location of(JsonParser parser) {
        return new LocationImpl(parser.getTokenLocation().getLineNr(), parser.getTokenLocation().getColumnNr());
    }

    static Location of(int line, int column) {
        return new LocationImpl(line, column);
    }

    static Location of(Mark mark) {
        return new LocationImpl(mark.getLine()+1, mark.getColumn()+1);
    }

    class LocationImpl implements Location {

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