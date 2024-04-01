package com.siglet.config.builder;

import com.siglet.SigletError;
import com.siglet.utils.Joining;
import com.siglet.utils.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlobalConfigBuilder {

    private List<ReceiverBuilder> receivers;

    private List<ExporterBuilder> exporters;

    private List<TracePipelineBuilder> pipelines;

    public List<ReceiverBuilder> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<ReceiverBuilder> receivers) {
        this.receivers = receivers;
    }

    public List<ExporterBuilder>  getExporters() {
        return exporters;
    }

    public void setExporters(List<ExporterBuilder> exporters) {
        this.exporters = exporters;
    }

    public List<TracePipelineBuilder> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<TracePipelineBuilder> pipelines) {
        this.pipelines = pipelines;
    }

    public void validateUniqueNames() {
        String notUniqueNames = Stream.of(
                        getReceivers().stream()
                                .map(ReceiverBuilder::getName),
                        getExporters().stream()
                                .map(ExporterBuilder::getName),
                        getPipelines().stream()
                                .map(TracePipelineBuilder::getName),
                        getPipelines().stream()
                                .flatMap(t -> t.getSpanletBuilders().stream())
                                .map(SpanletBuilder::getName)
                )
                .flatMap(s -> s)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(e -> "'" + e.getKey() + "' appears " + StringUtils.frequency(e.getValue()))
                .collect(Joining.twoDelimiters(", ", " and ",
                        "Names must be unique within the configuration file but: ", "!"));

        if (notUniqueNames != null) {
            throw new SigletError(notUniqueNames);
        }
    }

}
