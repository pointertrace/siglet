package com.siglet.config;

import com.siglet.config.builder.*;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.spanlet.SpanletCheckerDiscriminator;
import com.siglet.spanlet.SpanletTypes;

import static com.siglet.config.parser.schema.SchemaFactory.*;

public class GlobalConfigCheckFactory {


    public static NodeChecker receiversChecker() {

        return array(
                strictObject(GrpcReceiverBuilder::new,
                        requiredProperty(GrpcReceiverBuilder::setName, "grpc", text()),
                        requiredProperty(GrpcReceiverBuilder::setAddress, "address", text(inetSocketAddress()))
                )
        );

    }

    public static NodeChecker grpcExportersChecker() {

        return array(
                strictObject(GrpcExporterBuilder::new,
                        requiredProperty(GrpcExporterBuilder::setName, "grpc", text()),
                        requiredProperty(GrpcExporterBuilder::setAddress, "address", text(inetSocketAddress()))
                )
        );
    }

    public static NodeChecker spanletChecker() {

        return strictObject(SpanletBuilder::new,
                requiredProperty(SpanletBuilder::setName, "spanlet", text()),
                alternativeRequiredProperty("to",
                        requiredProperty(SpanletBuilder::setTo, "to", array(text())),
                        requiredProperty(SpanletBuilder::setToSingleValue, "to", text())),
                alternativeRequiredProperty("from",
                        requiredProperty(SpanletBuilder::setFrom, "from", array(text())),
                        requiredProperty(SpanletBuilder::setFromSingleValue, "from", text())),
                requiredProperty(SpanletBuilder::setType, "type", text()),
                requiredDynamicProperty("config", new SpanletCheckerDiscriminator(new SpanletTypes()))
        );
    }

    public static NodeChecker tracePipelineChecker() {
        return array(strictObject(TracePipelineBuilder::new,
                requiredProperty(TracePipelineBuilder::setName, "trace", text()),
                requiredProperty(TracePipelineBuilder::setSpanletBuilders, "pipeline",
                        array(spanletChecker()))));
    }

    public static NodeChecker globalConfigChecker() {
        return strictObject(GlobalConfigBuilder::new,
                requiredProperty(GlobalConfigBuilder::setReceivers, "receivers",
                        receiversChecker()),
                requiredProperty(GlobalConfigBuilder::setExporters, "exporters",
                        grpcExportersChecker()),
                requiredProperty(GlobalConfigBuilder::setPipelines, "pipelines",
                        tracePipelineChecker()));
    }
}
