package io.github.pointertrace.siglet.parser.located;

import java.util.Objects;

public interface Location {

    int getLine();

    int getColumn();

    String describe();

    static Location of(int line, int column) {
        return new LocationImpl(line, column);
    }

    class LocationImpl implements Location {

        private final int line;
        private final int column;

        public LocationImpl(int line, int column) {
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
        public String describe() {
            return "(" + getLine() + ":" + getColumn() + ")";
        }

        @Override
        public boolean equals(Object o) {
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
            return "(" + line + "," + column + ")";
        }
    }

}
