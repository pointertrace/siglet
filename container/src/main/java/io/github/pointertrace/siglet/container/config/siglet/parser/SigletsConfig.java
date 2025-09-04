package io.github.pointertrace.siglet.container.config.siglet.parser;


import io.github.pointertrace.siglet.parser.NodeValueBuilder;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record SigletsConfig(

        List<SigletConfig> sigletsConfig

) {

    public static class Builder implements NodeValueBuilder, Located {

        private Location location;

        private List<SigletConfig> sigletsConfig = new ArrayList<>();

        @Override
        public SigletsConfig build() {
            return new SigletsConfig(Collections.unmodifiableList(sigletsConfig));
        }

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public void setLocation(Location location) {
            this.location = location;
        }

        public void setSigletsConfig(List<SigletConfig> sigletsConfig) {
            if (sigletsConfig != null) {
                this.sigletsConfig.addAll(sigletsConfig);
            }
        }

    }
}

