package com.siglet.config.builder;

import java.util.List;

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

}
