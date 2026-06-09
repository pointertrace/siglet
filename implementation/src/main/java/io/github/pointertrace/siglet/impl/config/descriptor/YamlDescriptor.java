package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.validator.ComposedValidator;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;
import io.github.pointertrace.siglet.parser.*;

import java.util.ArrayList;
import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;

public class YamlDescriptor {

    private GlobalConfigDescriptor globalConfigDescriptor = new GlobalConfigDescriptor();

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
                        GlobalConfigDescriptor.descriptorSchemaBuilder()).customErrorMessage("Error in global configuration:", ""))
                .addProperty(property("receivers", YamlDescriptor::setReceivers, array(ArrayList::new,
                        arrayItem(List::add, ReceiverDescriptor.descriptorSchemaBuilder(receiverTypeRegistry)
                                .customErrorMessage("Error in receiver at #location:", "#location Receiver must be an object"))
                                .customErrorMessage("Error in receivers array item at #location:"))
                        .customErrorMessage("Error in receivers at #location:", "#location Receivers must be an array"))
                        .customErrorMessage("Error in receivers at #location:", "#location There must be at least one receiver"))
                .addProperty(property("pipelines", YamlDescriptor::setPipelines, array(ArrayList::new,
                        arrayItem(List::add, PipelineDescriptor.descriptorSchemaBuilder(processorRegistry)
                                .customErrorMessage("Error in pipeline at #location:", "#location Pipeline must be an object"))
                                .customErrorMessage("Error in pipelines array item at #location:"))
                        .customErrorMessage("Error in pipelines at #location:", "#location Pipelines must be an array")
                ).customErrorMessage("Error in pipelines at #location:", "#location There must be at least one pipeline"))
                .addOptionalProperty(property("exporters", YamlDescriptor::setExporters, array(ArrayList::new,
                        arrayItem(List::add, ExporterDescriptor.descriptorSchemaBuilder(exporterRegistry)
                                .customErrorMessage("Error in exporter at #location:", "#location Exporter must be an object"))
                                .customErrorMessage("Error in exporters array item at #location:"))
                        .customErrorMessage("Error in exporters at #location:", "#location Exporters must be an array"))
                        .customErrorMessage("Error in exporters at #location:", "#location There must be at least one exporter"))
                .customErrorMessage("Error in configuration:");
    }

    public static YamlDescriptor parse(String yaml) {
        return parse(yaml, new ReceiverTypeRegistry(), new ProcessorTypeRegistry(), new ExporterTypeRegistry());
    }

    public static YamlDescriptor parse(String yaml, ReceiverTypeRegistry receiverTypeRegistry,
                                       ProcessorTypeRegistry processorRegistry, ExporterTypeRegistry exporterRegistry) {
        if (yaml == null || yaml.isBlank()) {
            throw new SigletError("Siglet config file is empty");
        }
        try {
            Node node = Parser.DEFAULT.parse(yaml);
            Schema schema = descriptorSchemaBuilder(receiverTypeRegistry, processorRegistry, exporterRegistry).build();
            Factory factory = schema.validate(node);
            return factory.create(YamlDescriptor.class);
        } catch (SchemaException e) {
            throw new SigletError(e.getMessage(), e);
        }

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
