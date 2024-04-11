package com.siglet.config.item;

import com.siglet.SigletError;
import com.siglet.config.item.repository.NodeRepository;
import com.siglet.utils.Joining;
import com.siglet.utils.StringUtils;
import org.apache.camel.builder.RouteBuilder;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigItem {

    private List<ReceiverItem> receiverItems;

    private List<ExporterItem> exporterItems;

    private List<TracePipelineItem> pipelines;

    public List<ReceiverItem> getReceivers() {
        return receiverItems;
    }

    public void setReceivers(List<ReceiverItem> receiverItems) {
        this.receiverItems = receiverItems;
    }

    public List<ExporterItem> getExporters() {
        return exporterItems;
    }

    public void setExporters(List<ExporterItem> exporterItems) {
        this.exporterItems = exporterItems;
    }

    public List<TracePipelineItem> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<TracePipelineItem> pipelines) {
        this.pipelines = pipelines;
    }

    public void validateUniqueNames() {
        String notUniqueNames = Stream.of(
                        getReceivers().stream()
                                .map(ReceiverItem::getName),
                        getExporters().stream()
                                .map(ExporterItem::getName),
                        getPipelines().stream()
                                .map(TracePipelineItem::getName),
                        getPipelines().stream()
                                .flatMap(p -> p.getProcessors().stream())
                                .map(SpanletItem::getName)
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

        getReceivers().forEach(nodeRepository::addItem);
        getExporters().forEach(nodeRepository::addItem);
        getPipelines().forEach(nodeRepository::addItem);
        getPipelines().stream()
                .flatMap(pipelines -> pipelines.getProcessors().stream())
                .forEach(nodeRepository::addItem);

        nodeRepository.connect();

        return nodeRepository;
    }

    public RouteBuilder build() {
        NodeRepository nodeRepository = createRepository();
        return nodeRepository.createRouteBuilder();
    }

}
