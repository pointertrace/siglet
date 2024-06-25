package com.siglet.config.item;

import com.siglet.SigletError;
import com.siglet.config.item.repository.NodeRepository;
import com.siglet.utils.Joining;
import com.siglet.utils.StringUtils;
import org.apache.camel.builder.RouteBuilder;
import org.checkerframework.checker.units.qual.A;

import java.util.List;
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

        nodeRepository.connect();

        return nodeRepository;
    }


    public RouteBuilder build() {
        NodeRepository nodeRepository = createRepository();
        return nodeRepository.createRouteBuilder();
    }

    public Stream<? extends ProcessorItem> getItems(PipelineItem<?> pipline) {
        System.out.println(pipline.getClass().getTypeName());
        return pipline.getProcessors().getValue().stream();


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
}
