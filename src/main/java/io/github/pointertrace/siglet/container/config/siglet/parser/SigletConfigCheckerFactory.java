package io.github.pointertrace.siglet.container.config.siglet.parser;

import io.github.pointertrace.siget.parser.NodeChecker;
import io.github.pointertrace.siget.parser.NodeCheckerFactory;
import io.github.pointertrace.siglet.api.Processor;
import io.github.pointertrace.siglet.container.config.raw.LocatedStringTransformer;
import io.github.pointertrace.siglet.container.config.siglet.SigletConfig;
import io.github.pointertrace.siglet.container.config.siglet.SigletsConfig;

import static io.github.pointertrace.siget.parser.schema.SchemaFactory.*;


public class SigletConfigCheckerFactory {


    public NodeChecker create(ClassLoader classLoader) {
        return
                strictObject(SigletsConfig.Builder::new,
                        requiredProperty(SigletsConfig.Builder::setSigletsConfig,
                                SigletsConfig.Builder::setLocation,
                                "siglets",
                                array(
                                        strictObject(SigletConfig.Builder::new,
                                                requiredProperty(SigletConfig.Builder::setName, SigletConfig.Builder::setNameLocation, "name", text()),
                                                requiredProperty(SigletConfig.Builder::setDescription, SigletConfig.Builder::setDescriptionLocation,
                                                        "description", text()),
                                                requiredProperty(SigletConfig.Builder::setSiglet, SigletConfig.Builder::setSigletLocation,
                                                        "siglet-class",
                                                        text(new ClassValueTransformer(Processor.class, classLoader))),
                                                optionalProperty(SigletConfig.Builder::setConfigFactory,
                                                        SigletConfig.Builder::setConfigCheckerFactoryClassLocation,
                                                        "checker-factory-class",
                                                        text(new ClassValueTransformer(NodeCheckerFactory.class, classLoader))),
                                                optionalProperty(SigletConfig.Builder::setDestinations,
                                                        SigletConfig.Builder::setDestinationsLocation,
                                                        "destinations",
                                                        array(text(new LocatedStringTransformer())))
                                        ))));
    }

}
