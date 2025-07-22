package com.siglet.container.config.graph;

import com.siglet.container.config.raw.ExporterConfig;

import java.util.ArrayList;
import java.util.List;

public final class ExporterNode extends BaseNode {

    private List<BaseNode> from = new ArrayList<>();

    public ExporterNode(ExporterConfig exporterConfig) {
        super(exporterConfig);
    }

    public List<BaseNode> getFrom() {
        return from;
    }

    public void setFrom(List<BaseNode> from) {
        this.from = from;
    }

    @Override
    public ExporterConfig getConfig() {
        return (ExporterConfig) super.getConfig();
    }

//    public void calculateEventLoopConfig(EventLoopConfig globalEventLoopConfig) {
//        if (getConfig() instanceof GrpcExporterConfig grpcExporterConfig) {
//            EventLoopConfig eventLoopConfig = globalEventLoopConfig.chain(grpcExporterConfig);
//            this.queueSize = eventLoopConfig.getQueueSize();
//            this.threadPoolSize = eventLoopConfig.getThreadPoolSize();
//        }
//
//    }
}
