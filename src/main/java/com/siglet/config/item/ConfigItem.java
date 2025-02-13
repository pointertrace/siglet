package com.siglet.config.item;

import com.siglet.SigletError;
import com.siglet.config.item.repository.NodeRepository;
import com.siglet.config.located.Location;
import com.siglet.utils.Joining;
import com.siglet.utils.StringUtils;
import org.apache.camel.builder.RouteBuilder;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigItem extends Item {

    private List<ReceiverItem> receivers;

    private Location receiversLocation;

    private List<ExporterItem> exporters;

    private Location exportersLocation;

    private List<PipelineItem> pipelines;

    private Location pipelinesLocation;

    public List<ReceiverItem> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<ReceiverItem> receiverItems) {
        this.receivers = receiverItems;
    }

    public List<ExporterItem> getExporters() {
        return exporters;
    }

    public void setExporters(List<ExporterItem> exporterItems) {
        this.exporters = exporterItems;
    }

    public List<PipelineItem> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<PipelineItem> pipelines) {
        this.pipelines = pipelines;
    }

    protected void validateUniqueNames() {
        String notUniqueNames = Stream.of(
                        getReceivers().stream()
                                .map(ReceiverItem::getName),
                        getExporters().stream()
                                .map(ExporterItem::getName),
                        getPipelines().stream()
                                .map(PipelineItem::getName),
                        getPipelines().stream()
                                .flatMap(p -> p.getSiglets().stream())
                                .map(SigletItem::getName)
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

    public Stream<SigletItem> getItems(PipelineItem pipeline) {
        return pipeline.getSiglets().stream();
    }

    @Override
    public void afterSetValues() {
        getReceivers().forEach(Item::afterSetValues);
        getExporters().forEach(Item::afterSetValues);
        getPipelines().forEach(Item::afterSetValues);
        getPipelines().stream()
                .flatMap(this::getItems)
                .forEach(Item::afterSetValues);
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  config:");
        sb.append("\n");

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(receiversLocation.describe());
        sb.append("  receivers:");
        sb.append("\n");
        for(ReceiverItem receiver: receivers) {
            sb.append(receiver.describe(level + 2));
        }

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(exportersLocation.describe());
        sb.append("  exporters:");
        sb.append("\n");
        for(ExporterItem exporter: exporters) {
            sb.append(exporter.describe(level + 2));
        }

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(pipelinesLocation.describe());
        sb.append("  pipelines:");
        sb.append("\n");
        for(PipelineItem pipeline:pipelines) {
            sb.append(pipeline.describe(level + 2));
        }

        return sb.toString();
    }

    public Location getReceiversLocation() {
        return receiversLocation;
    }

    public void setReceiversLocation(Location receiversLocation) {
        this.receiversLocation = receiversLocation;
    }

    public Location getExportersLocation() {
        return exportersLocation;
    }

    public void setExportersLocation(Location exportersLocation) {
        this.exportersLocation = exportersLocation;
    }

    public Location getPipelinesLocation() {
        return pipelinesLocation;
    }

    public void setPipelinesLocation(Location pipelinesLocation) {
        this.pipelinesLocation = pipelinesLocation;
    }
}
