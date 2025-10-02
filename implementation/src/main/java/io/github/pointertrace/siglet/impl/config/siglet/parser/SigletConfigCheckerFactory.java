package io.github.pointertrace.siglet.impl.config.siglet.parser;

import io.github.pointertrace.siglet.impl.config.raw.LocatedStringTransformer;
import io.github.pointertrace.siglet.parser.NodeChecker;

import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.*;


public class SigletConfigCheckerFactory {


    public NodeChecker create() {
        return strictObject(SigletsConfig.Builder::new,
                requiredProperty(SigletsConfig.Builder::setSigletsConfig,
                        SigletsConfig.Builder::setLocation,
                        "siglets",
                        array(
                                strictObject(SigletConfig.Builder::new,
                                        requiredProperty(SigletConfig.Builder::setName, SigletConfig.Builder::setNameLocation, "name", text()),
                                        requiredProperty(SigletConfig.Builder::setDescription, SigletConfig.Builder::setDescriptionLocation,
                                                "description", text()),
                                        requiredProperty(SigletConfig.Builder::setSigletClassName, SigletConfig.Builder::setSigletLocation,
                                                "siglet-class",
                                                text()),
                                        optionalProperty(SigletConfig.Builder::setConfigCheckerFactoryClassName,
                                                SigletConfig.Builder::setConfigCheckerFactoryClassLocation,
                                                "checker-factory-class",
                                                text()),
                                        optionalProperty(SigletConfig.Builder::setDestinations,
                                                SigletConfig.Builder::setDestinationsLocation,
                                                "destinations",
                                                array(text(new LocatedStringTransformer())))
                                ))));
    }

}
