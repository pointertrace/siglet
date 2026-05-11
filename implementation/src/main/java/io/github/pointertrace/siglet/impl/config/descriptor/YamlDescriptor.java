package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.impl.config.descriptor.validator.ComposedValidator;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;
import io.github.pointertrace.siglet.parser.Factory;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.Parser;
import io.github.pointertrace.siglet.parser.Schema;

import java.util.ArrayList;
import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;

public class YamlDescriptor {

    private GlobalConfigDescriptor globalConfigDescriptor;

    private List<ReceiverDescriptor> receiverDescriptors = new ArrayList<>();

    private List<ExporterDescriptor> exporterDescriptors = new ArrayList<>();

    private List<PipelineDescriptor> pipelineDescriptors = new ArrayList<>();

    public GlobalConfigDescriptor getGlobalConfig() {
        return globalConfigDescriptor;
    }

    public void setGlobalConfig(GlobalConfigDescriptor globalConfigDescriptor) {
        this.globalConfigDescriptor = globalConfigDescriptor;
    }

    public List<ReceiverDescriptor> getReceivers() {
        return List.copyOf(receiverDescriptors);
    }

    public void setReceivers(List<ReceiverDescriptor> receiverConfigDescriptors) {
        this.receiverDescriptors = List.copyOf(receiverConfigDescriptors);
    }

    public List<ExporterDescriptor> getExporters() {
        return List.copyOf(exporterDescriptors);
    }

    public void setExporters(List<ExporterDescriptor> exporterConfigDescriptors) {
        this.exporterDescriptors = List.copyOf(exporterConfigDescriptors);
    }

    public List<PipelineDescriptor> getPipelines() {
        return List.copyOf(pipelineDescriptors);
    }

    public void setPipelines(List<PipelineDescriptor> pipelineDescriptors) {
        this.pipelineDescriptors = List.copyOf(pipelineDescriptors);
    }


    public static Schema.Builder<?, YamlDescriptor> descriptorSchemaBuilder(
            ReceiverTypeRegistry receiverTypeRegistry, ProcessorTypeRegistry processorRegistry,
            ExporterTypeRegistry exporterRegistry) {

        return object(YamlDescriptor::new)
                .addOptionalProperty(property("global", YamlDescriptor::setGlobalConfig,
                        GlobalConfigDescriptor.descriptorSchemaBuilder()))
                .addProperty(property("receivers", YamlDescriptor::setReceivers, array(ArrayList::new,
                        arrayItem(List::add, ReceiverDescriptor.descriptorSchemaBuilder(receiverTypeRegistry)))))
                .addProperty(property("pipelines", YamlDescriptor::setPipelines, array(ArrayList::new,
                        arrayItem(List::add, PipelineDescriptor.descriptorSchemaBuilder(processorRegistry)))))
                .addOptionalProperty(property("exporters", YamlDescriptor::setExporters, array(ArrayList::new,
                        arrayItem(List::add, ExporterDescriptor.descriptorSchemaBuilder(exporterRegistry)))));
    }

    public static YamlDescriptor parse(String yaml) {
        return parse(yaml, new ReceiverTypeRegistry(), new ProcessorTypeRegistry(), new ExporterTypeRegistry());
    }

    public static YamlDescriptor parse(String yaml, ReceiverTypeRegistry receiverTypeRegistry,
                                       ProcessorTypeRegistry processorRegistry, ExporterTypeRegistry exporterRegistry) {
        Node node = Parser.DEFAULT.parse(yaml);
        Schema schema = descriptorSchemaBuilder(receiverTypeRegistry, processorRegistry, exporterRegistry).build();
        Factory factory = schema.validate(node);
        return  factory.create(YamlDescriptor.class);

    }

    public static void validate(YamlDescriptor yamlDescriptor) {
        ComposedValidator validator = new ComposedValidator();
        validator.validate(yamlDescriptor);
    }

    public static YamlDescriptor create(String yaml) {
        YamlDescriptor yamlDescriptor = parse(yaml);
        validate(yamlDescriptor);
        return yamlDescriptor;
    }



    public void afterSetValues() {
        getReceivers().forEach(BaseDescriptor::afterSetValues);
        getExporters().forEach(BaseDescriptor::afterSetValues);
        getPipelines().forEach(BaseDescriptor::afterSetValues);
        getPipelines().stream()
                .flatMap(pipeline -> pipeline.getProcessors().stream())
                .forEach(BaseDescriptor::afterSetValues);

//        getPipelines().stream()
//                .flatMap(pipeline -> pipeline.getProcessors().stream())
//                .forEach(processorConfig -> processorConfig.setRawConfig(this));

//        getExporters().stream()
//                .forEach(exporterConfig -> exporterConfig.setRawConfig(this));

    }


}
