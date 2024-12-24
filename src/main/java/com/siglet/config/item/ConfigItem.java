package com.siglet.config.item;

import com.siglet.SigletError;
import com.siglet.config.item.repository.NodeRepository;
import com.siglet.utils.Joining;
import com.siglet.utils.StringUtils;
import org.apache.camel.builder.RouteBuilder;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigItem extends Item {

    private ArrayItem<ReceiverItem> receiverItems;

    private ArrayItem<ExporterItem> exporterItems;

    private ArrayItem<PipelineItem<?>> pipelines;

    public ArrayItem<ReceiverItem> getReceivers() {
        return receiverItems;
    }

    public void setReceivers(ArrayItem<ReceiverItem> receiverItems) {
        this.receiverItems = receiverItems;
    }

    public ArrayItem<ExporterItem> getExporters() {
        return exporterItems;
    }

    public void setExporters(ArrayItem<ExporterItem> exporterItems) {
        this.exporterItems = exporterItems;
    }

    public ArrayItem<PipelineItem<?>> getPipelines() {
        return pipelines;
    }

    public void setPipelines(ArrayItem<PipelineItem<?>> pipelines) {
        this.pipelines = pipelines;
    }

    protected void validateUniqueNames() {
        String notUniqueNames = Stream.of(
                        getReceivers().getValue().stream()
                                .map(ReceiverItem::getName),
                        getExporters().getValue().stream()
                                .map(ExporterItem::getName),
                        getPipelines().getValue().stream()
                                .map(PipelineItem::getName),
                        getPipelines().getValue().stream()
                                .flatMap(p -> p.getProcessors().getValue().stream())
                                .map(ProcessorItem::getName)
                )
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(e -> "'" + e.getKey() + "' appears " + StringUtils.frequency(e.getValue()))
                .collect(Joining.twoDelimiters(", ", " and ",
                        "Names must be unique within the configuration file but: ", "!"));

        if (notUniqueNames != null) {
            throw new SigletError(notUniqueNames);
        }

    }

    protected NodeRepository createRepository() {
        NodeRepository nodeRepository = new NodeRepository();


        getReceivers().getValue().forEach(nodeRepository::addItem);
        getExporters().getValue().forEach(nodeRepository::addItem);
        getPipelines().getValue().forEach(nodeRepository::addItem);
        getPipelines().getValue().stream()
                .flatMap(this::getItems)
                .forEach(nodeRepository::addItem);

        nodeRepository.addItem(new DropExporterItem());
        nodeRepository.connect();

        return nodeRepository;
    }


    public RouteBuilder build() {
        NodeRepository nodeRepository = createRepository();
        return nodeRepository.createRouteBuilder();
    }

    public Stream<? extends ProcessorItem> getItems(PipelineItem<?> pipeline) {
        return pipeline.getProcessors().getValue().stream();
    }

    @Override
    public void afterSetValues() {
        getReceivers().getValue().forEach(Item::afterSetValues);
        getExporters().getValue().forEach(Item::afterSetValues);
        getPipelines().getValue().forEach(Item::afterSetValues);
        getPipelines().getValue().stream()
                .flatMap(this::getItems)
                .forEach(Item::afterSetValues);
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  ConfigItem");
        sb.append("\n");

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(receiverItems.getLocation().describe());
        sb.append("  receiverItems");
        sb.append("\n");
        sb.append(receiverItems.describe(level + 2));

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(exporterItems.getLocation().describe());
        sb.append("  exporterItems");
        sb.append("\n");
        sb.append(exporterItems.describe(level + 2));

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(pipelines.getLocation().describe());
        sb.append("  pipelines");
        sb.append("\n");
        sb.append(pipelines.describe(level + 2));

        return sb.toString();
    }
}
