package io.github.pointertrace.siglet.impl.engine.component.connection.drop;

import io.github.pointertrace.siglet.impl.config.descriptor.ExporterDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.exporter.BaseExporter;
import io.github.pointertrace.siglet.parser.StringValue;

public class DropComponent extends BaseExporter {

    public DropComponent(SigletContext sigletContext) {
        super(sigletContext,new ExporterNode(createDescriptor()));
    }

    private static ExporterDescriptor createDescriptor() {
        ExporterDescriptor exporterDescriptor = new ExporterDescriptor();
        exporterDescriptor.setName(new StringValue("DROP"));
        return exporterDescriptor;
    }

    @Override
    protected void doStart() {

    }

    @Override
    protected void doStop() {

    }

    @Override
    public SignalDestination getSignalDestination() {
        return new DropSignalDestination(getSigletContext());
    }





}
