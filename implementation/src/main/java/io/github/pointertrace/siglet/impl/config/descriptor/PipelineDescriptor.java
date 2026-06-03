package io.github.pointertrace.siglet.impl.config.descriptor;


import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.parser.Schema;
import io.github.pointertrace.siglet.parser.StringValue;

import java.util.ArrayList;
import java.util.List;

import static io.github.pointertrace.siglet.impl.config.descriptor.SchemaBuilderUtils.destinationSchemaBuilder;
import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;

public class PipelineDescriptor extends BaseDescriptor {

    private StringValue from;

    private List<StringValue> start;

    private List<ProcessorDescriptor> processorDescriptors;

    public StringValue getFrom() {
        return from;
    }

    protected void setFrom(StringValue from) {
        this.from = from;
    }

    public List<StringValue> getStart() {
        return List.copyOf(start);
    }

    protected void setStart(List<StringValue> start) {
        this.start = List.copyOf(start);
    }

    public List<ProcessorDescriptor> getProcessors() {
        return processorDescriptors;
    }

    protected void setProcessors(List<ProcessorDescriptor> processorDescriptors) {
        this.processorDescriptors = List.copyOf(processorDescriptors);
        processorDescriptors.forEach(p -> p.setPipelineName(getName().getValue()));
    }

    @Override
    public void validate() {
        super.validate();
        start.stream().map(StringValue::getValue).filter(s -> s.equals(getName().getValue())).findAny().ifPresent(s -> {
            throw new SigletError(String.format("Pipeline [%s] at %s has an auto reference.",
                    getName().getValue(), getLocation().print()));
        });
        if (getFrom() != null && getFrom().getValue().equals(getName().getValue())) {
            throw new SigletError(String.format("Pipeline [%s] at %s has an auto reference.",
                    getName().getValue(), getLocation().print()));
        }
    }
    public static Schema.Builder<?,PipelineDescriptor> descriptorSchemaBuilder(ProcessorTypeRegistry registry) {
        return object(PipelineDescriptor::new)
                .addProperty(property("name", PipelineDescriptor::setName, stringValueObject().customErrorMessage("#location Pipeline name must be a string"))
                        .customErrorMessage("Invalid pipeline name property at #location:","#location Missing pipeline 'name' property"))
                .addProperty(optionalProperty("from", PipelineDescriptor::setFrom, stringValueObject().customErrorMessage("#location Pipeline from must be a string"))
                        .customErrorMessage("Invalid pipeline from property at #location:","#location Missing pipeline 'from' property"))
                .addProperty(destinationSchemaBuilder("start", PipelineDescriptor::setStart))
                .addProperty(property("processors", PipelineDescriptor::setProcessors, array(ArrayList::new,
                        arrayItem(List::add, ProcessorDescriptor.descriptorSchemaBuilder(registry)).customErrorMessage("Error in processor at #location:","")).customErrorMessage("Error in processors at #location:", "#location Processors must be an array"))
                        .customErrorMessage("Error in processors at #location:", "#location There must be an array with at least one processor")
                );
    }

}

