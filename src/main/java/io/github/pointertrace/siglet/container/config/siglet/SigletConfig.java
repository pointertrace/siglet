package io.github.pointertrace.siglet.container.config.siglet;

import io.github.pointertrace.siget.parser.NodeChecker;
import io.github.pointertrace.siget.parser.NodeCheckerFactory;
import io.github.pointertrace.siget.parser.NodeValueBuilder;
import io.github.pointertrace.siget.parser.located.Located;
import io.github.pointertrace.siget.parser.located.Location;
import io.github.pointertrace.siget.parser.node.SigletParserError;
import io.github.pointertrace.siglet.api.Processor;
import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.container.config.raw.LocatedString;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public record SigletConfig(

        String name,
        Location nameLocation,
        String description,
        Location descriptionLocation,
        Class<? extends Processor> sigletClass,
        Location sigletLocation,
        NodeChecker configChecker,
        Location configCheckerFactoryClassLocation,
        List<LocatedString> destinations) {

    public Processor createSigletInstance() {
        try {
            return sigletClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new SigletParserError(String.format("Error creating instance of spanlet %s: %s", name,
                    e.getMessage()), sigletLocation);
        }
    }

    public Method getSigletMethod() {
        if (Spanlet.class.isAssignableFrom(sigletClass)) {
            try {
                return Spanlet.class.getMethod("span", Span.class, ProcessorContext.class,
                        ResultFactory.class);
            } catch (NoSuchMethodException e) {
                throw new SigletParserError(String.format("Spanlet %s is %s but does not have a span method",
                        name, sigletClass.getName()), sigletLocation);
            }
        } else {
            throw new SigletParserError(String.format("Spanlet %s is not an instance of SpanletProcessor",
                    name), sigletLocation);
        }

    }

    public static class Builder implements NodeValueBuilder, Located {

        private Location location;

        private String name;

        private Location nameLocation;

        private String description;

        private Location descriptionLocation;

        private Class<? extends Processor> siglet;

        private Location sigletLocation;

        private Class<? extends NodeCheckerFactory> configCheckerFactory;

        private Location configCheckerFactoryClassLocation;

        private final List<LocatedString> destinations = new ArrayList<>();

        private Location destinationsLocation;

        @Override
        public Object build() {

            try {
                NodeChecker configChecker = null;
                if (configCheckerFactory != null) {
                    configChecker = configCheckerFactory.getConstructor().newInstance().create();
                }
                return new SigletConfig(name, nameLocation, description, descriptionLocation, siglet, sigletLocation,
                        configChecker, configCheckerFactoryClassLocation, destinations);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new SigletParserError(String.format("Error creating instance of ConfigCheckerFactory for " +
                        "spanlet %s: %s", name, e.getMessage()), configCheckerFactoryClassLocation);
            }

        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setSiglet(Class<? extends Processor> siglet) {
            this.siglet = siglet;
            return this;
        }

        public Builder setConfigFactory(Class<? extends NodeCheckerFactory> configCheckerFactory) {
            this.configCheckerFactory = configCheckerFactory;
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

