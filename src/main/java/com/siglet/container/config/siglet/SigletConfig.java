package com.siglet.container.config.siglet;

import com.siglet.api.Processor;
import com.siglet.api.ProcessorContext;
import com.siglet.api.ResultFactory;
import com.siglet.api.modifiable.trace.ModifiableSpan;
import com.siglet.api.modifiable.trace.ModifiableSpanlet;
import com.siglet.api.parser.NodeChecker;
import com.siglet.api.parser.NodeCheckerFactory;
import com.siglet.api.parser.NodeValueBuilder;
import com.siglet.api.parser.located.Located;
import com.siglet.api.parser.located.Location;
import com.siglet.api.unmodifiable.trace.UnmodifiableSpan;
import com.siglet.api.unmodifiable.trace.UnmodifiableSpanlet;
import com.siglet.parser.node.SigletParserError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public record SigletConfig(
        String name,
        Location nameLocation,
        String description,
        Location descriptionLocation,
        Class<? extends Processor> sigletClass,
        Location sigletLocation,
        NodeChecker configChecker,
        Location configCheckerFactoryClassLocation) {

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
        if (ModifiableSpanlet.class.isAssignableFrom(sigletClass)) {
            try {
                return ModifiableSpanlet.class.getMethod("span", ModifiableSpan.class, ProcessorContext.class,
                        ResultFactory.class);
            } catch (NoSuchMethodException e) {
                throw new SigletParserError(String.format("Spanlet %s is %s but does not have a span method",
                        name, sigletClass.getName()), sigletLocation);
            }
        } else if (UnmodifiableSpanlet.class.isAssignableFrom(sigletClass)) {
            try {
                return UnmodifiableSpanlet.class.getMethod("span", UnmodifiableSpan.class, ProcessorContext.class,
                        ResultFactory.class);
            } catch (NoSuchMethodException e) {
                throw new SigletParserError(String.format("Spanlet %s is %s but does not have a span method",
                        name, sigletClass.getName()), sigletLocation);
            }
        } else {
            throw new SigletParserError(String.format("Spanlet %s is not an instance of UnmodifiableSpanletProcessor",
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


        @Override
        public Object build() {

            try {
                NodeCheckerFactory configCheckerFactoryInstance = configCheckerFactory.getConstructor().newInstance();
                return new SigletConfig(name, nameLocation, description, descriptionLocation, siglet, sigletLocation,
                        configCheckerFactoryInstance.create(), configCheckerFactoryClassLocation);
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

