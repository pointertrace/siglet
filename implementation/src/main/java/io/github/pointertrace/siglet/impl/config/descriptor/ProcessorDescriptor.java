package io.github.pointertrace.siglet.impl.config.descriptor;


import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.parser.IntegerValue;
import io.github.pointertrace.siglet.parser.Schema;
import io.github.pointertrace.siglet.parser.StringValue;

import java.util.ArrayList;
import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;

public class ProcessorDescriptor extends ConfigurableDescriptor {

    private List<StringValue> to = new ArrayList<>();

    private IntegerValue queueSize;

    private IntegerValue threadPoolSize;

    private String pipelineName;

    public List<StringValue> getTo() {
        return List.copyOf(to);
    }

    protected void setTo(List<StringValue> to) {
        this.to = List.copyOf(to);
    }

    public IntegerValue getQueueSize() {
        return queueSize;
    }

    protected void setQueueSize(IntegerValue queueSize) {
        this.queueSize = queueSize;
    }

    public IntegerValue getThreadPoolSize() {
        return threadPoolSize;
    }

    protected void setThreadPoolSize(IntegerValue threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    protected void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    @Override
    public void validate() {
        super.validate();
        to.stream().map(StringValue::getValue).filter(s -> s.equals(getName().getValue())).findAny().ifPresent(s -> {
            throw new SigletError(String.format("Processor [%s] at %s has an auto reference.",
                    getName().getValue(), getLocation().print()));
        });
    }

    public static Schema.Builder<?, ProcessorDescriptor> descriptorSchemaBuilder(ProcessorTypeRegistry registry) {
        return object(ProcessorDescriptor::new)
                .addProperty(registry.getPropertySwitchSchema(ProcessorDescriptor::setName, ProcessorDescriptor::setType))
                .addOptionalProperty(property("queue-size", ProcessorDescriptor::setQueueSize, integerValueObject()))
                .addOptionalProperty(property("thread-pool-size", ProcessorDescriptor::setThreadPoolSize, integerValueObject()))
                .addOptionalProperty(SchemaBuilderUtils.destinationSchemaBuilder("to", ProcessorDescriptor::setTo));
    }

}
