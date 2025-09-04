package io.github.pointertrace.siglet.container.config.siglet.parser;

import io.github.pointertrace.siglet.parser.NodeValueBuilder;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;
import io.github.pointertrace.siglet.container.config.raw.LocatedString;

import java.util.ArrayList;
import java.util.List;


public record SigletConfig(
        String name,
        Location nameLocation,
        String description,
        Location descriptionLocation,
        String sigletClassName,
        Location sigletLocation,
        String configCheckerFactoryClassName,
        Location configCheckerFactoryClassLocation,
        List<LocatedString> destinations,
        Location destinationsLocation) {


    public static class Builder implements NodeValueBuilder, Located {

        private Location location;

        private String name;

        private Location nameLocation;

        private String description;

        private Location descriptionLocation;

        private String sigletClassName;

        private Location sigletLocation;

        private String configCheckerFactoryClassName;

        private Location configCheckerFactoryClassLocation;

        private final List<LocatedString> destinations = new ArrayList<>();

        private Location destinationsLocation;

        @Override
        public Object build() {
            return new SigletConfig(name, nameLocation, description, descriptionLocation, sigletClassName, sigletLocation,
                    configCheckerFactoryClassName, configCheckerFactoryClassLocation, destinations, destinationsLocation);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setSigletClassName(String sigletClassName) {
            this.sigletClassName = sigletClassName;
            return this;
        }

        public Builder setConfigCheckerFactoryClassName(String configCheckerFactoryClassName) {
            this.configCheckerFactoryClassName = configCheckerFactoryClassName;
            return this;
        }

        public Builder setNameLocation(Location nameLocation) {
            this.nameLocation = nameLocation;
            return this;
        }

        public Builder setDescriptionLocation(Location descriptionLocation) {
            this.descriptionLocation = descriptionLocation;
            return this;
        }

        public Builder setSigletLocation(Location sigletLocation) {
            this.sigletLocation = sigletLocation;
            return this;
        }

        public Builder setConfigCheckerFactoryClassLocation(Location configCheckerFactoryClassLocation) {
            this.configCheckerFactoryClassLocation = configCheckerFactoryClassLocation;
            return this;
        }

        public Builder setDestinations(List<LocatedString> destinations) {
            this.destinations.addAll(destinations);
            return this;
        }

        public Builder setDestinationsLocation(Location destinationsLocation) {
            this.destinationsLocation = destinationsLocation;
            return this;
        }

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public void setLocation(Location location) {
            this.location = location;
        }

    }
}

