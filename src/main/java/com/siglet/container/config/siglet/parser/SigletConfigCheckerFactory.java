package com.siglet.container.config.siglet.parser;

import com.siglet.api.Processor;
import com.siglet.api.parser.NodeChecker;
import com.siglet.api.parser.NodeCheckerFactory;
import com.siglet.container.config.siglet.SigletConfig;

import static com.siglet.parser.schema.SchemaFactory.*;

public class SigletConfigCheckerFactory {


    public NodeChecker create(ClassLoader classLoader) {
        return strictObject(SigletConfig.Builder::new,
                requiredProperty(SigletConfig.Builder::setName, SigletConfig.Builder::setNameLocation, "name", text()),
                requiredProperty(SigletConfig.Builder::setDescription, SigletConfig.Builder::setDescriptionLocation,
                        "description", text()),
                requiredProperty(SigletConfig.Builder::setSiglet, SigletConfig.Builder::setSigletLocation,
                        "siglet-class", text(new ClassValueTransformer(Processor.class,classLoader))),
                requiredProperty(SigletConfig.Builder::setConfigFactory,
                        SigletConfig.Builder::setConfigCheckerFactoryClassLocation,
                        "checker-factory-class", text(new ClassValueTransformer(NodeCheckerFactory.class,classLoader)))

        );
    }
}
